package org.yejt.cacheclient.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.yejt.cacheclient.annotation.CachePut;
import org.yejt.cacheclient.annotation.CacheRemove;
import org.yejt.cacheclient.annotation.Cacheable;

import java.lang.annotation.Annotation;

@Aspect
public class CacheAspect
{
    //TODO: Wrap with 3 annotations in package annotation
    @Pointcut(value = "@annotation(org.yejt.cacheclient.annotation.Cacheable)")
    public void cacheable() {}

    @Pointcut(value = "@annotation(org.yejt.cacheclient.annotation.CachePut)")
    public void cachePut() {}

    @Pointcut(value = "@annotation(org.yejt.cacheclient.annotation.CacheRemove)")
    public void cacheRemove() {}

    @Around(value = "cacheable()")
    public Object cacheableAspect(ProceedingJoinPoint joinPoint)
    {
        Cacheable annotation = getAnnotation(joinPoint, Cacheable.class);
        try
        {
            return joinPoint.proceed();
        }
        catch (Throwable throwable)
        {
            throwable.printStackTrace();
            throw new RuntimeException(throwable);
        }
    }

    @Around(value = "cachePut()")
    public Object cachePutAspect(ProceedingJoinPoint joinPoint)
    {
        CachePut annotation = getAnnotation(joinPoint, CachePut.class);
        try
        {
            return joinPoint.proceed();
        }
        catch (Throwable throwable)
        {
            throwable.printStackTrace();
            throw new RuntimeException(throwable);
        }
    }

    @Around("cacheRemove()")
    public Object cacheRemoveAspect(ProceedingJoinPoint joinPoint)
    {
        CacheRemove annotation = getAnnotation(joinPoint, CacheRemove.class);
        try
        {
            return joinPoint.proceed();
        }
        catch (Throwable throwable)
        {
            throwable.printStackTrace();
            throw new RuntimeException(throwable);
        }
    }

    private static <T extends Annotation> T getAnnotation
            (ProceedingJoinPoint joinPoint, Class<T> cls)
    {
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        return signature.getMethod().getAnnotation(cls);
    }

}
