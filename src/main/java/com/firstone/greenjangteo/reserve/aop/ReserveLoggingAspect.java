package com.firstone.greenjangteo.reserve.aop;

import com.firstone.greenjangteo.reserve.dto.request.AddReserveRequestDto;
import com.firstone.greenjangteo.utility.MemoryUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import static com.firstone.greenjangteo.aop.LogConstant.PERFORMANCE_MEASUREMENT;

@Component
@Aspect
public class ReserveLoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(ReserveLoggingAspect.class);

    private static final String ADD_RESERVE_POINTCUT
            = "execution(* com.firstone.greenjangteo.user.domain.token.service.*.*(..)) && args(addReserveRequestDto)";
    private static final String ADD_RESERVE_START
            = "Beginning to '{}.{}' task by userId: '{}', addedReserve: '{}'";
    private static final String ADD_RESERVE_END
            = "'{}.{}' task was executed successfully by userId: '{}', addedReserve: '{}', ";

    @Around(ADD_RESERVE_POINTCUT)
    public Object logAroundForAddReserveRequestDto(ProceedingJoinPoint joinPoint, AddReserveRequestDto addReserveRequestDto)
            throws Throwable {

        log.info(ADD_RESERVE_START,
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(),
                addReserveRequestDto.getUserId(), addReserveRequestDto.getAddedReserve());

        long beforeMemory = MemoryUtil.usedMemory();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object process = joinPoint.proceed();

        stopWatch.stop();
        long memoryUsage = MemoryUtil.usedMemory() - beforeMemory;

        log.info(ADD_RESERVE_END + PERFORMANCE_MEASUREMENT,
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(),
                addReserveRequestDto.getUserId(), addReserveRequestDto.getAddedReserve(),
                stopWatch.getTotalTimeMillis(), memoryUsage);

        return process;
    }
}
