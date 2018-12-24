package org.yejt.cacheclient.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yejt.cacheclient.annotation.CachePut;
import org.yejt.cacheclient.annotation.CacheRemove;
import org.yejt.cacheclient.annotation.Cacheable;
import org.yejt.cacheclient.client.XXCacheClient;
import org.yejt.cacheclient.codec.CacheCodec;
import org.yejt.cacheclient.condition.CacheCondition;
import org.yejt.cacheclient.keygen.KeyGenerator;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

//TODO: add codec
@Aspect
@Component
public class CacheAspect
{
    @Autowired
    private XXCacheClient cacheClient;

    @Autowired
    private ObjectMapper mapper;

    private ThreadLocal<Map<Class<? extends KeyGenerator>, KeyGenerator>> keyGeneratorCache
            = ThreadLocal.withInitial(HashMap::new);

    private ThreadLocal<Map<Class<? extends CacheCondition>, CacheCondition>> conditionCache
            = ThreadLocal.withInitial(HashMap::new);

    private ThreadLocal<Map<Class<? extends CacheCodec>, CacheCodec>> codecCache
            = ThreadLocal.withInitial(HashMap::new);

    @Pointcut(value = "@annotation(org.yejt.cacheclient.annotation.Cacheable)")
    public void cacheable() {}

    @Pointcut(value = "@annotation(org.yejt.cacheclient.annotation.CachePut)")
    public void cachePut() {}

    @Pointcut(value = "@annotation(org.yejt.cacheclient.annotation.CacheRemove)")
    public void cacheRemove() {}

    @Around(value = "cacheable()")
    public Object cacheableAspect(ProceedingJoinPoint joinPoint)
            throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException
    {
        Cacheable annotation = getAnnotation(joinPoint, Cacheable.class);
        Object[] params = joinPoint.getArgs();
        KeyGenerator keyGenerator = getAndCacheKeyGenerator(annotation.keyGenerator());
        CacheCondition cacheCondition = getAndCacheCondition(annotation.condition());
        // First fetch cache
        Object cache = cacheClient.get(annotation.cacheName(),
                keyGenerator.generateKey(joinPoint.getTarget(), params));
        if(cache != null)
            return unwrap(cache, getReturnType(joinPoint));

        return cachePut(joinPoint, params, keyGenerator, cacheCondition, annotation.cacheName());
    }

    @Around(value = "cachePut()")
    public Object cachePutAspect(ProceedingJoinPoint joinPoint)
            throws IllegalAccessException, InstantiationException,
            NoSuchMethodException, InvocationTargetException
    {
        CachePut annotation = getAnnotation(joinPoint, CachePut.class);
        Object[] params = joinPoint.getArgs();
        KeyGenerator keyGenerator = getAndCacheKeyGenerator(annotation.keyGenerator());
        CacheCondition cacheCondition = getAndCacheCondition(annotation.condition());

        return cachePut(joinPoint, params, keyGenerator, cacheCondition, annotation.cacheName());
    }



    @Around("cacheRemove()")
    public Object cacheRemoveAspect(ProceedingJoinPoint joinPoint)
            throws IllegalAccessException, InstantiationException,
            NoSuchMethodException, InvocationTargetException
    {
        CacheRemove annotation = getAnnotation(joinPoint, CacheRemove.class);
        Object[] params = joinPoint.getArgs();
        KeyGenerator keyGenerator = getAndCacheKeyGenerator(annotation.keyGenerator());
        CacheCondition cacheCondition = getAndCacheCondition(annotation.condition());
        if(keyGenerator == null || cacheCondition == null)
            return null;
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

    private KeyGenerator getAndCacheKeyGenerator(Class<? extends KeyGenerator> clazz)
            throws IllegalAccessException, InstantiationException,
            NoSuchMethodException, InvocationTargetException
    {
        KeyGenerator keyGenerator;
        Map<Class<? extends KeyGenerator>, KeyGenerator> keyGeneratorHashMap
                = keyGeneratorCache.get();
        if(keyGeneratorHashMap.containsKey(clazz))
            keyGenerator = keyGeneratorCache.get().get(clazz);
        else
        {
            keyGenerator = clazz.getDeclaredConstructor().newInstance();
            keyGeneratorHashMap.put(clazz, keyGenerator);
        }

        return keyGenerator;
    }

    private CacheCondition getAndCacheCondition(Class<? extends CacheCondition> clazz)
            throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException
    {
        CacheCondition cacheCondition;
        if(conditionCache.get().containsKey(clazz))
            cacheCondition = conditionCache.get().get(clazz);
        else
        {
            cacheCondition = clazz.getDeclaredConstructor().newInstance();
            conditionCache.get().put(clazz, cacheCondition);
        }

        return cacheCondition;
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
