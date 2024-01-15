package com.firstone.greenjangteo.post.aop;

import com.firstone.greenjangteo.post.domain.comment.dto.CommentRequestDto;
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
public class CommentLoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(CommentLoggingAspect.class);

    private static final String CREATE_COMMENT_POINTCUT
            = "execution(* com.firstone.greenjangteo.post.domain.comment.service.*.*(..)) && args(commentRequestDto)";
    private static final String CREATE_COMMENT_START
            = "Beginning to '{}.{}' task by userId: '{}', content: '{}'";
    private static final String CREATE_COMMENT_END
            = "'{}.{}' task was executed successfully by userId: '{}', content: '{}', ";

    @Around(CREATE_COMMENT_POINTCUT)
    public Object logAroundForCommentRequestDto(ProceedingJoinPoint joinPoint,
                                                CommentRequestDto commentRequestDto) throws Throwable {
        log.info(CREATE_COMMENT_START,
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(), commentRequestDto.getUserId(), commentRequestDto.getContent());

        long beforeMemory = MemoryUtil.usedMemory();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object process = joinPoint.proceed();

        stopWatch.stop();
        long memoryUsage = MemoryUtil.usedMemory() - beforeMemory;

        log.info(CREATE_COMMENT_END + PERFORMANCE_MEASUREMENT,
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(), commentRequestDto.getUserId(), commentRequestDto.getContent(),
                stopWatch.getTotalTimeMillis(), memoryUsage);

        return process;
    }
}
