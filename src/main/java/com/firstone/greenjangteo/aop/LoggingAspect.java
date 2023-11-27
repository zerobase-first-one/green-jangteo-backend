package com.firstone.greenjangteo.aop;

import com.firstone.greenjangteo.utility.MemoryUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
@Aspect
public class LoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("execution(* com.firstone.greenjangteo..service.*.*(..)) && args(stringValue, ..)")
    public Object logAroundForStringValue(ProceedingJoinPoint joinPoint, String stringValue) throws Throwable {
        String parameterName = ((CodeSignature) joinPoint.getSignature()).getParameterNames()[0];

        log.info("Beginning to '{}.{}' task by {}: '{}'",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(), parameterName, stringValue);

        long beforeMemory = MemoryUtil.usedMemory();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object process = joinPoint.proceed();

        stopWatch.stop();
        long memoryUsage = MemoryUtil.usedMemory() - beforeMemory;

        log.info("'{}.{}' task was executed successfully by '{}: {}', estimated time: {} ms, used memory: {} bytes",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(), parameterName, stringValue,
                stopWatch.getTotalTimeMillis(), memoryUsage);

        return process;
    }

    @Around("execution(* com.firstone.greenjangteo..service.*.*(..)) && args(longValue, ..)")
    public Object logAroundForLongValue(ProceedingJoinPoint joinPoint, Long longValue) throws Throwable {
        String parameterName = ((CodeSignature) joinPoint.getSignature()).getParameterNames()[0];

        log.info("Beginning to '{}.{}' task by {}: '{}'",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(), parameterName, longValue);

        long beforeMemory = MemoryUtil.usedMemory();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object process = joinPoint.proceed();

        stopWatch.stop();
        long memoryUsage = MemoryUtil.usedMemory() - beforeMemory;

        log.info("'{}.{}' task was executed successfully by '{}: {}', estimated time: {} ms, used memory: {} bytes",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(), parameterName, longValue,
                stopWatch.getTotalTimeMillis(), memoryUsage);

        return process;
    }
}
