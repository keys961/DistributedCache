package org.yejt.cacheclient.aspect;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yejt.cacheclient.annotation.CachePut;
import org.yejt.cacheclient.annotation.CacheRemove;
import org.yejt.cacheclient.annotation.Cacheable;
import org.yejt.cacheclient.client.XXCacheClient;
import org.yejt.cacheclient.condition.CacheCondition;
import org.yejt.cacheclient.keygen.KeyGenerator;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
public class CacheAspect
{
    @Autowired
    private XXCacheClient cacheClient;

    @Autowired
    private ObjectMapper mapper;

    private static final KryoPool KRYO_POOL
            = new KryoPool.Builder(() ->
    {
        Kryo kryo = new Kryo();
        kryo.setReferences(true);
        kryo.setRegistrationRequired(false);
        ((Kryo.DefaultInstantiatorStrategy) kryo.getInstantiatorStrategy())
                .setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());
        return kryo;
    }).build();

    private Map<Class<? extends KeyGenerator>, KeyGenerator> keyGeneratorCache
            = new ConcurrentHashMap<>();

    private Map<Class<? extends CacheCondition>, CacheCondition> conditionCache
            = new ConcurrentHashMap<>();

    @Pointcut(value = "@annotation(org.yejt.cacheclient.annotation.Cacheable)")
    public void cacheable() {}

    @Pointcut(value = "@annotation(org.yejt.cacheclient.annotation.CachePut)")
    public void cachePut() {}

    @Pointcut(value = "@annotation(org.yejt.cacheclient.annotation.CacheRemove)")
    public void cacheRemove() {}

    @Around(value = "cacheable()")
    public Object cacheableAspect(ProceedingJoinPoint joinPoint)
            throws IllegalAccessException, InstantiationException
    {
        Cacheable annotation = getAnnotation(joinPoint, Cacheable.class);
        Object[] params = joinPoint.getArgs();
        KeyGenerator keyGenerator = null;
        CacheCondition cacheCondition = null;

        if(keyGeneratorCache.containsKey(annotation.keyGenerator()))
            keyGenerator = keyGeneratorCache.get(annotation.keyGenerator());
        else
        {
            keyGenerator = annotation.keyGenerator().newInstance();
            keyGeneratorCache.put(annotation.keyGenerator(), keyGenerator);
        }

        if(conditionCache.containsKey(annotation.condition()))
            cacheCondition = conditionCache.get(annotation.condition());
        else
        {
            cacheCondition = annotation.condition().newInstance();
            conditionCache.put(annotation.condition(), cacheCondition);
        }

        // First fetch cache
        Object cache = cacheClient.get(annotation.cacheName(),
                keyGenerator.generateKey(joinPoint.getTarget(), params));
        if(cache != null)
            return unwrap(cache, getReturnType(joinPoint));

        return cachePut(joinPoint, params, keyGenerator, cacheCondition, annotation.cacheName());
    }

    @Around(value = "cachePut()")
    public Object cachePutAspect(ProceedingJoinPoint joinPoint)
            throws IllegalAccessException, InstantiationException
    {
        CachePut annotation = getAnnotation(joinPoint, CachePut.class);
        Object[] params = joinPoint.getArgs();
        KeyGenerator keyGenerator = null;
        CacheCondition cacheCondition = null;

        if(keyGeneratorCache.containsKey(annotation.keyGenerator()))
            keyGenerator = keyGeneratorCache.get(annotation.keyGenerator());
        else
        {
            keyGenerator = annotation.keyGenerator().newInstance();
            keyGeneratorCache.put(annotation.keyGenerator(), keyGenerator);
        }

        if(conditionCache.containsKey(annotation.condition()))
            cacheCondition = conditionCache.get(annotation.condition());
        else
        {
            cacheCondition = annotation.condition().newInstance();
            conditionCache.put(annotation.condition(), cacheCondition);
        }

        return cachePut(joinPoint, params, keyGenerator, cacheCondition, annotation.cacheName());
    }



    @Around("cacheRemove()")
    public Object cacheRemoveAspect(ProceedingJoinPoint joinPoint)
            throws IllegalAccessException, InstantiationException
    {
        CacheRemove annotation = getAnnotation(joinPoint, CacheRemove.class);
        Object[] params = joinPoint.getArgs();
        KeyGenerator keyGenerator = null;
        CacheCondition cacheCondition = null;

        if(keyGeneratorCache.containsKey(annotation.keyGenerator()))
            keyGenerator = keyGeneratorCache.get(annotation.keyGenerator());
        else
        {
            keyGenerator = annotation.keyGenerator().newInstance();
            keyGeneratorCache.put(annotation.keyGenerator(), keyGenerator);
        }

        if(conditionCache.containsKey(annotation.condition()))
            cacheCondition = conditionCache.get(annotation.condition());
        else
        {
            cacheCondition = annotation.condition().newInstance();
            conditionCache.put(annotation.condition(), cacheCondition);
        }

        Object result = null;
        try
        {
            result = joinPoint.proceed();
            String cacheName = annotation.cacheName();
            String key = keyGenerator.generateKey(joinPoint.getTarget(), params);
            if(key != null && cacheCondition.condition(result, params))
                cacheClient.remove(cacheName, key);
            return result;
        }
        catch (Throwable throwable)
        {
            throwable.printStackTrace();
            if(result == null)
                return null;
            return result;
        }
    }

    private Object cachePut(ProceedingJoinPoint joinPoint,
                            Object[] params, KeyGenerator keyGenerator, CacheCondition cacheCondition, String s)
    {
        Object result = null;
        try
        {
            result = joinPoint.proceed();
            String key = keyGenerator.generateKey(joinPoint.getTarget(), params);
            if(result != null && key != null
                    && cacheCondition.condition(result, params))
                cacheClient.put(s, key, result);
            return result;
        }
        catch (Throwable throwable)
        {
            throwable.printStackTrace();
            if(result == null)
                return null;
            return result;
        }
    }

    private <T extends Annotation> T getAnnotation
            (ProceedingJoinPoint joinPoint, Class<T> cls)
    {
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        return signature.getMethod().getAnnotation(cls);
    }

    private Class getReturnType(ProceedingJoinPoint joinPoint)
    {
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        return signature.getReturnType();
    }
    // TODO: Optimize the serialization/deserialization configuration
    private  <T> T unwrap(Object value, Class<T> cls)
    {
        return mapper.convertValue(value, cls);
    }
}
