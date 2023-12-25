package com.firstone.greenjangteo.order.aop;

import com.firstone.greenjangteo.order.dto.request.CartOrderRequestDto;
import com.firstone.greenjangteo.order.dto.request.OrderRequestDto;
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
public class OrderLoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(OrderLoggingAspect.class);

    private static final String ORDER_POINTCUT
            = "execution(* com.firstone.greenjangteo.order.service.*.*(..)) && args(orderRequestDto)";
    private static final String ORDER_START
            = "Beginning to '{}.{}' task by sellerId: '{}', buyerId: '{}'";
    private static final String ORDER_END
            = "'{}.{}' task was executed successfully by 'sellerId: {}', 'buyerId: {}', ";

    private static final String CART_ORDER_POINTCUT
            = "execution(* com.firstone.greenjangteo.order.service.*.*(..)) && args(cartOrderRequestDto)";
    private static final String CART_ORDER_START
            = "Beginning to '{}.{}' task by buyerId: '{}', cartId: '{}'";
    private static final String CART_ORDER_END
            = "'{}.{}' task was executed successfully by 'buyerId: {}', 'cartId: {}', ";

    @Around(ORDER_POINTCUT)
    public Object logAroundForOrderRequestDto(ProceedingJoinPoint joinPoint,
                                              OrderRequestDto orderRequestDto) throws Throwable {
        log.info(ORDER_START,
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(), orderRequestDto.getSellerId(), orderRequestDto.getBuyerId());

        long beforeMemory = MemoryUtil.usedMemory();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object process = joinPoint.proceed();

        stopWatch.stop();
        long memoryUsage = MemoryUtil.usedMemory() - beforeMemory;

        log.info(ORDER_END + PERFORMANCE_MEASUREMENT,
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(), orderRequestDto.getSellerId(), orderRequestDto.getBuyerId(),
                stopWatch.getTotalTimeMillis(), memoryUsage);

        return process;
    }

    @Around(CART_ORDER_POINTCUT)
    public Object logAroundForCartOrderRequestDto(ProceedingJoinPoint joinPoint,
                                                  CartOrderRequestDto cartOrderRequestDto) throws Throwable {
        log.info(CART_ORDER_START,
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(), cartOrderRequestDto.getBuyerId(), cartOrderRequestDto.getCartId());

        long beforeMemory = MemoryUtil.usedMemory();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object process = joinPoint.proceed();

        stopWatch.stop();
        long memoryUsage = MemoryUtil.usedMemory() - beforeMemory;

        log.info(CART_ORDER_END + PERFORMANCE_MEASUREMENT,
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(), cartOrderRequestDto.getBuyerId(), cartOrderRequestDto.getCartId(),
                stopWatch.getTotalTimeMillis(), memoryUsage);

        return process;
    }
}
