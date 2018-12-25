package org.yejt.cacheclient.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Objects;

/**
 * @author keys961
 */
@Aspect
@Component
public class CacheAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheAspect.class);

    private final XXCacheClient cacheClient;

    private ThreadLocal<Map<Class<? extends KeyGenerator>, KeyGenerator>> keyGeneratorCache
            = ThreadLocal.withInitial(HashMap::new);

    private ThreadLocal<Map<Class<? extends CacheCondition>, CacheCondition>> conditionCache
            = ThreadLocal.withInitial(HashMap::new);

    private ThreadLocal<Map<Class<? extends CacheCodec>, CacheCodec>> codecCache
            = ThreadLocal.withInitial(HashMap::new);

    @Autowired
    public CacheAspect(XXCacheClient cacheClient) {
        this.cacheClient = cacheClient;
    }

    @Pointcut(value = "@annotation(org.yejt.cacheclient.annotation.Cacheable)")
    public void cacheable() {
    }

    @Pointcut(value = "@annotation(org.yejt.cacheclient.annotation.CachePut)")
    public void cachePut() {
    }

    @Pointcut(value = "@annotation(org.yejt.cacheclient.annotation.CacheRemove)")
    public void cacheRemove() {
    }

    @Around(value = "cacheable()")
    @SuppressWarnings("unchecked")
    public Object cacheableAspect(ProceedingJoinPoint joinPoint)
            throws IllegalAccessException, InstantiationException,
            NoSuchMethodException, InvocationTargetException {
        Cacheable annotation = getAnnotation(joinPoint, Cacheable.class);
        Object[] params = joinPoint.getArgs();
        KeyGenerator keyGenerator = getAndCacheKeyGenerator(annotation.keyGenerator());
        CacheCondition cacheCondition = getAndCacheCondition(annotation.condition());
        CacheCodec cacheCodec = getAndCacheCodec(annotation.codec());
        if (Objects.isNull(keyGenerator) || Objects.isNull(cacheCondition) || Objects.isNull(cacheCodec)) {
            try {
                return joinPoint.proceed(params);
            } catch (Throwable throwable) {
                LOGGER.error("Join point proceeded error: {}", throwable.getMessage());
                return null;
            }
        }
        // First fetch cache
        try {
            byte[] raw = cacheClient.get(annotation.cacheName(),
                    keyGenerator.generateKey(joinPoint.getTarget(), params));
            if (raw != null) {
                Object cache = cacheCodec.decode(raw);
                return unwrap(cache, getReturnType(joinPoint));
            }
        } catch (Exception e) {
            LOGGER.warn("Exception occurred: {}", e.getMessage());
        }

        return cachePut(joinPoint, params, keyGenerator,
                cacheCondition, cacheCodec,
                annotation.cacheName());
    }

    @Around(value = "cachePut()")
    public Object cachePutAspect(ProceedingJoinPoint joinPoint)
            throws IllegalAccessException, InstantiationException,
            NoSuchMethodException, InvocationTargetException {
        CachePut annotation = getAnnotation(joinPoint, CachePut.class);
        Object[] params = joinPoint.getArgs();
        KeyGenerator keyGenerator = getAndCacheKeyGenerator(annotation.keyGenerator());
        CacheCondition cacheCondition = getAndCacheCondition(annotation.condition());
        CacheCodec cacheCodec = getAndCacheCodec(annotation.codec());
        if (Objects.isNull(keyGenerator) || Objects.isNull(cacheCondition) || Objects.isNull(cacheCodec)) {
            try {
                return joinPoint.proceed(params);
            } catch (Throwable throwable) {
                LOGGER.error("Join point proceeded error: {}", throwable.getMessage());
                return null;
            }
        }
        return cachePut(joinPoint, params, keyGenerator,
                cacheCondition, cacheCodec,
                annotation.cacheName());
    }

    @Around("cacheRemove()")
    public Object cacheRemoveAspect(ProceedingJoinPoint joinPoint)
            throws IllegalAccessException, InstantiationException,
            NoSuchMethodException, InvocationTargetException {
        CacheRemove annotation = getAnnotation(joinPoint, CacheRemove.class);
        Object[] params = joinPoint.getArgs();
        KeyGenerator keyGenerator = getAndCacheKeyGenerator(annotation.keyGenerator());
        CacheCondition cacheCondition = getAndCacheCondition(annotation.condition());
        if (Objects.isNull(keyGenerator) || Objects.isNull(cacheCondition)) {
            try {
                return joinPoint.proceed(params);
            } catch (Throwable throwable) {
                LOGGER.error("Join point proceeded error: {}", throwable.getMessage());
                return null;
            }
        }
        Object result;
        try {
            result = joinPoint.proceed();
            String cacheName = annotation.cacheName();
            String key = keyGenerator.generateKey(joinPoint.getTarget(), params);
            if (key != null && cacheCondition.condition(result, params)) {
                cacheClient.remove(cacheName, key);
            }
            return result;
        } catch (Throwable throwable) {
            LOGGER.warn("Exception occurred: {}", throwable.getMessage());
            return null;
        }
    }

    private KeyGenerator getAndCacheKeyGenerator(Class<? extends KeyGenerator> clazz)
            throws IllegalAccessException, InstantiationException,
            NoSuchMethodException, InvocationTargetException {
        KeyGenerator keyGenerator;
        Map<Class<? extends KeyGenerator>, KeyGenerator> keyGeneratorHashMap
                = keyGeneratorCache.get();
        if (keyGeneratorHashMap.containsKey(clazz)) {
            keyGenerator = keyGeneratorCache.get().get(clazz);
        } else {
            keyGenerator = clazz.getDeclaredConstructor().newInstance();
            keyGeneratorHashMap.put(clazz, keyGenerator);
        }

        return keyGenerator;
    }

    private CacheCondition getAndCacheCondition(Class<? extends CacheCondition> clazz)
            throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        CacheCondition cacheCondition;
        if (conditionCache.get().containsKey(clazz)) {
            cacheCondition = conditionCache.get().get(clazz);
        } else {
            cacheCondition = clazz.getDeclaredConstructor().newInstance();
            conditionCache.get().put(clazz, cacheCondition);
        }

        return cacheCondition;
    }

    private CacheCodec getAndCacheCodec(Class<? extends CacheCodec> clazz)
            throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        if (codecCache.get().containsKey(clazz)) {
            return codecCache.get().get(clazz);
        }
        CacheCodec codec = clazz.getDeclaredConstructor().newInstance();
        codecCache.get().put(clazz, codec);
        return codec;
    }

    @SuppressWarnings("unchecked")
    private Object cachePut(ProceedingJoinPoint joinPoint,
                            Object[] params, KeyGenerator keyGenerator,
                            CacheCondition cacheCondition, CacheCodec codec,
                            String cacheName) {
        Object result;
        try {
            result = joinPoint.proceed(joinPoint.getArgs());
            String key = keyGenerator.generateKey(joinPoint.getTarget(), params);
            if (result != null && key != null && cacheCondition.condition(result, params)) {
                byte[] raw = codec.encode(result);
                cacheClient.put(cacheName, key, raw);
            }
            return result;
        } catch (Throwable throwable) {
            LOGGER.warn("Exception occurred: {}", throwable.getMessage());
            return null;
        }
    }

    private <T extends Annotation> T getAnnotation
            (ProceedingJoinPoint joinPoint, Class<T> cls) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod().getAnnotation(cls);
    }

    private Class getReturnType(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getReturnType();
    }

    private <T> T unwrap(Object value, Class<T> cls) {
        return cls.cast(value);
    }
}
