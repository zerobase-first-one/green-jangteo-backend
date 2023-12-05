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

import static com.firstone.greenjangteo.aop.LogConstant.PERFORMANCE_MEASUREMENT;

@Component
@Aspect
public class LoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    private static final String STRING_VALUE_POINTCUT
            = "execution(* com.firstone.greenjangteo..service.*.*(..)) && args(stringValue, ..)";

    private static final String LONG_VALUE_POINTCUT
            = "execution(* com.firstone.greenjangteo..service.*.*(..)) && args(longValue, ..)";

    private static final String START
            = "Beginning to '{}.{}' task by {}: '{}'";
    private static final String END
            = "'{}.{}' task was executed successfully by '{}: {}', ";

    @Around(STRING_VALUE_POINTCUT)
    public Object logAroundForStringValue(ProceedingJoinPoint joinPoint, String stringValue) throws Throwable {
        String parameterName = ((CodeSignature) joinPoint.getSignature()).getParameterNames()[0];

        log.info(START,
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(), parameterName, stringValue);

        long beforeMemory = MemoryUtil.usedMemory();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object process = joinPoint.proceed();

        stopWatch.stop();
        long memoryUsage = MemoryUtil.usedMemory() - beforeMemory;

        log.info(END + PERFORMANCE_MEASUREMENT,
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(), parameterName, stringValue,
                stopWatch.getTotalTimeMillis(), memoryUsage);

        return process;
    }

    @Around(LONG_VALUE_POINTCUT)
    public Object logAroundForLongValue(ProceedingJoinPoint joinPoint, Long longValue) throws Throwable {
        String parameterName = ((CodeSignature) joinPoint.getSignature()).getParameterNames()[0];

        log.info(START,
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(), parameterName, longValue);

        long beforeMemory = MemoryUtil.usedMemory();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object process = joinPoint.proceed();

        stopWatch.stop();
        long memoryUsage = MemoryUtil.usedMemory() - beforeMemory;

        log.info(END + PERFORMANCE_MEASUREMENT,
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(), parameterName, longValue,
                stopWatch.getTotalTimeMillis(), memoryUsage);

        return process;
    }
}
