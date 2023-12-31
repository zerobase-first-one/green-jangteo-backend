package com.firstone.greenjangteo.post.aop;

import com.firstone.greenjangteo.post.dto.PostRequestDto;
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
public class PostLoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(PostLoggingAspect.class);

    private static final String CREATE_POST_POINTCUT
            = "execution(* com.firstone.greenjangteo.post.service.*.*(..)) && args(postRequestDto)";
    private static final String CREATE_POST_START
            = "Beginning to '{}.{}' task by userId: '{}', subject: '{}'";
    private static final String CREATE_POST_END
            = "'{}.{}' task was executed successfully by 'userId: {}', subject: '{}', ";

    @Around(CREATE_POST_POINTCUT)
    public Object logAroundForPostRequestDto(ProceedingJoinPoint joinPoint,
                                             PostRequestDto postRequestDto) throws Throwable {
        log.info(CREATE_POST_START,
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(), postRequestDto.getUserId(), postRequestDto.getSubject());

        long beforeMemory = MemoryUtil.usedMemory();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object process = joinPoint.proceed();

        stopWatch.stop();
        long memoryUsage = MemoryUtil.usedMemory() - beforeMemory;

        log.info(CREATE_POST_END + PERFORMANCE_MEASUREMENT,
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(), postRequestDto.getUserId(), postRequestDto.getSubject(),
                stopWatch.getTotalTimeMillis(), memoryUsage);

        return process;
    }
}
