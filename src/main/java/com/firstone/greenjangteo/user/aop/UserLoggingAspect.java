package com.firstone.greenjangteo.user.aop;

import com.firstone.greenjangteo.user.form.SignInForm;
import com.firstone.greenjangteo.user.form.SignUpForm;
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
public class UserLoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(UserLoggingAspect.class);

    private static final String SIGN_UP_POINTCUT
            = "execution(* com.firstone.greenjangteo.user.service.*.*(..)) && args(signUpForm)";
    private static final String SIGN_UP_START
            = "Beginning to '{}.{}' task by email: '{}', username: '{}'";
    private static final String SIGN_UP_END
            = "'{}.{}' task was executed successfully by email: '{}', username: '{}', ";

    private static final String SIGN_IN_POINTCUT
            = "execution(* com.firstone.greenjangteo.user.service.*.*(..)) && args(signInForm)";
    private static final String SIGN_IN_START
            = "Beginning to '{}.{}' task by email or username: '{}'";
    private static final String SIGN_IN_END
            = "'{}.{}' task was executed successfully by email or username: '{}', ";

    @Around(SIGN_UP_POINTCUT)
    public Object logAroundForSignUpForm(ProceedingJoinPoint joinPoint, SignUpForm signUpForm) throws Throwable {

        log.info(SIGN_UP_START,
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(), signUpForm.getEmail(), signUpForm.getUsername());

        long beforeMemory = MemoryUtil.usedMemory();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object process = joinPoint.proceed();

        stopWatch.stop();
        long memoryUsage = MemoryUtil.usedMemory() - beforeMemory;

        log.info(SIGN_UP_END + PERFORMANCE_MEASUREMENT,
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(), signUpForm.getEmail(), signUpForm.getUsername(),
                stopWatch.getTotalTimeMillis(), memoryUsage);

        return process;
    }

    @Around(SIGN_IN_POINTCUT)
    public Object logAroundForSignInForm(ProceedingJoinPoint joinPoint, SignInForm signInForm) throws Throwable {
        log.info(SIGN_IN_START,
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(), signInForm.getEmailOrUsername());

        long beforeMemory = MemoryUtil.usedMemory();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object process = joinPoint.proceed();

        stopWatch.stop();
        long memoryUsage = MemoryUtil.usedMemory() - beforeMemory;

        log.info(SIGN_IN_END + PERFORMANCE_MEASUREMENT,
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(), signInForm.getEmailOrUsername(),
                stopWatch.getTotalTimeMillis(), memoryUsage);

        return process;
    }
}
