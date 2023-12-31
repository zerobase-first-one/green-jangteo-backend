package com.firstone.greenjangteo.post.aop;

import com.firstone.greenjangteo.post.model.entity.Post;
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
public class ImageLoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(ImageLoggingAspect.class);

    private static final String SAVE_IMAGES_POINTCUT
            = "execution(* com.firstone.greenjangteo.post.domain.image.service.*.*(..)) && args(post, ..)";
    private static final String SAVE_IMAGES_START
            = "Beginning to '{}.{}' task by postId: '{}'";
    private static final String SAVE_IMAGES_END
            = "'{}.{}' task was executed successfully by 'postId: {}', ";

    @Around(SAVE_IMAGES_POINTCUT)
    public Object logAroundForPostAndImageRequestDtos(ProceedingJoinPoint joinPoint, Post post) throws Throwable {
        log.info(SAVE_IMAGES_START,
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(), post.getId());

        long beforeMemory = MemoryUtil.usedMemory();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object process = joinPoint.proceed();

        stopWatch.stop();
        long memoryUsage = MemoryUtil.usedMemory() - beforeMemory;

        log.info(SAVE_IMAGES_END + PERFORMANCE_MEASUREMENT,
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(), post.getId(),
                stopWatch.getTotalTimeMillis(), memoryUsage);

        return process;
    }
}
