package org.yejt.cachedemo.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogAspect
{
    private static final Logger LOGGER = LoggerFactory.getLogger(LogAspect.class);

    @Pointcut("@annotation(org.yejt.cachedemo.annotation.Log)")
    public void logPointCut() { }

    @Around("logPointCut()")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable
    {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long currentTime = System.currentTimeMillis();
        LOGGER.info("Method {} executed in {} milliseconds.",
                joinPoint.getSignature().getName(),
                currentTime - startTime);

        return result;
    }
}
