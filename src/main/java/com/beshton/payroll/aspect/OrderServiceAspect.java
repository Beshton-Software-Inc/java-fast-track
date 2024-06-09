package com.beshton.payroll.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class OrderServiceAspect {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceAspect.class);

    // Pointcut to match all methods in OrderService
    @Pointcut("execution(* com.beshton.payroll.service.OrderService.*(..))")
    private void orderServiceMethods() {}

    @Around("orderServiceMethods()")
    public Object profileMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            long duration = System.currentTimeMillis() - start;
            logger.info("{} completed in {} ms", joinPoint.getSignature().getName(), duration);
        }
    }

    @AfterThrowing(pointcut = "orderServiceMethods()", throwing = "ex")
    public void handleExceptions(JoinPoint joinPoint, Throwable ex) {
        logger.error("Exception in method {}: {}", joinPoint.getSignature().getName(), ex.getMessage());
    }
}
