package com.firstone.greenjangteo.user.aop;

import com.firstone.greenjangteo.user.form.SignUpForm;
import com.firstone.greenjangteo.user.utility.MemoryUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
@Aspect
public class UserLoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(UserLoggingAspect.class);

    @Around("execution(* com.firstone.greenjangteo..service.*.*(..)) && args(signUpForm)")
    public Object logAroundForSignUpForm(ProceedingJoinPoint joinPoint, SignUpForm signUpForm) throws Throwable {

        log.info("Beginning to '{}.{}' task by email: '{}', username: '{}'",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(), signUpForm.getEmail(), signUpForm.getUsername());

        long beforeMemory = MemoryUtil.usedMemory();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object process = joinPoint.proceed();

        stopWatch.stop();
        long memoryUsage = MemoryUtil.usedMemory() - beforeMemory;

        log.info("'{}.{}' task was executed successfully by 'email: {}', 'username: {}', estimated time: {} ms, used memory: {} bytes",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(), signUpForm.getEmail(), signUpForm.getUsername(),
                stopWatch.getTotalTimeMillis(), memoryUsage);

        return process;
    }
}
