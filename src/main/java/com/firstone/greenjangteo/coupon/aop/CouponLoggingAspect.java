package com.firstone.greenjangteo.coupon.aop;

import com.firstone.greenjangteo.coupon.dto.request.IssueCouponsRequestDto;
import com.firstone.greenjangteo.coupon.dto.request.ProvideCouponsToUserRequestDto;
import com.firstone.greenjangteo.coupon.dto.request.ProvideCouponsToUsersRequestDto;
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
public class CouponLoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(CouponLoggingAspect.class);

    private static final String CREATE_COUPONS_POINTCUT
            = "execution(* com.firstone.greenjangteo.coupon.service.*.*(..)) && args(issueCouponsRequestDto)";
    private static final String CREATE_COUPONS_START
            = "Beginning to '{}.{}' task by couponName: '{}'";
    private static final String CREATE_COUPONS_END
            = "'{}.{}' task was executed successfully by couponName: '{}', ";

    private static final String PROVIDE_COUPONS_TO_USER_POINTCUT
            = "execution(* com.firstone.greenjangteo.coupon.service.*.*(..)) && args(provideCouponsToUserRequestDto)";
    private static final String PROVIDE_COUPONS_TO_USER_START
            = "Beginning to '{}.{}' task by couponName: '{}', userId: '{}'";
    private static final String PROVIDE_COUPONS_TO_USER_END
            = "'{}.{}' task was executed successfully by couponName: '{}', userId: '{}', ";

    private static final String PROVIDE_COUPONS_TO_USERS_POINTCUT
            = "execution(* com.firstone.greenjangteo.coupon.service.*.*(..)) && args(provideCouponsToUsersRequestDto)";
    private static final String PROVIDE_COUPONS_TO_USERS_START
            = "Beginning to '{}.{}' task by couponGroupId: '{}'";
    private static final String PROVIDE_COUPONS_TO_USERS_END
            = "'{}.{}' task was executed successfully by couponGroupId: '{}', ";

    @Around(CREATE_COUPONS_POINTCUT)
    public Object logAroundForIssueCouponsRequestDto(ProceedingJoinPoint joinPoint,
                                                     IssueCouponsRequestDto issueCouponsRequestDto) throws Throwable {
        log.info(CREATE_COUPONS_START,
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(), issueCouponsRequestDto.getCouponName());

        long beforeMemory = MemoryUtil.usedMemory();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object process = joinPoint.proceed();

        stopWatch.stop();
        long memoryUsage = MemoryUtil.usedMemory() - beforeMemory;

        log.info(CREATE_COUPONS_END + PERFORMANCE_MEASUREMENT,
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(), issueCouponsRequestDto.getCouponName(),
                stopWatch.getTotalTimeMillis(), memoryUsage);

        return process;
    }

    @Around(PROVIDE_COUPONS_TO_USERS_POINTCUT)
    public Object logAroundForProvideCouponsToUsersRequestDto
            (ProceedingJoinPoint joinPoint, ProvideCouponsToUsersRequestDto provideCouponsToUsersRequestDto)
            throws Throwable {
        log.info(PROVIDE_COUPONS_TO_USERS_START,
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(), provideCouponsToUsersRequestDto.getCouponGroupId());

        long beforeMemory = MemoryUtil.usedMemory();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object process = joinPoint.proceed();

        stopWatch.stop();
        long memoryUsage = MemoryUtil.usedMemory() - beforeMemory;

        log.info(PROVIDE_COUPONS_TO_USERS_END + PERFORMANCE_MEASUREMENT,
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(), provideCouponsToUsersRequestDto.getCouponGroupId(),
                stopWatch.getTotalTimeMillis(), memoryUsage);

        return process;
    }

    @Around(PROVIDE_COUPONS_TO_USER_POINTCUT)
    public Object logAroundForProvideCouponsToUserRequestDto(ProceedingJoinPoint joinPoint,
                                                             ProvideCouponsToUserRequestDto provideCouponsToUserRequestDto)
            throws Throwable {
        log.info(PROVIDE_COUPONS_TO_USER_START,
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(), provideCouponsToUserRequestDto.getCouponName(),
                provideCouponsToUserRequestDto.getUser().getId());

        long beforeMemory = MemoryUtil.usedMemory();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object process = joinPoint.proceed();

        stopWatch.stop();
        long memoryUsage = MemoryUtil.usedMemory() - beforeMemory;

        log.info(PROVIDE_COUPONS_TO_USER_END + PERFORMANCE_MEASUREMENT,
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(),
                provideCouponsToUserRequestDto.getCouponName(), provideCouponsToUserRequestDto.getUser().getId(),
                stopWatch.getTotalTimeMillis(), memoryUsage);

        return process;
    }
}