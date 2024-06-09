package com.beshton.payroll.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class EmployeeServiceAspect {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceAspect.class);

    // Pointcut to match all methods in EmployeeService
    @Pointcut("execution(* com.beshton.payroll.service.EmployeeService.*(..))")
    private void employeeServiceMethods() {}

    @Before("employeeServiceMethods()")
    public void logMethodCall(JoinPoint joinPoint) {
        logger.info("Method called: {} with arguments: {}", joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
    }

    @Around("employeeServiceMethods()")
    public Object profileMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            long duration = System.currentTimeMillis() - start;
            logger.info("{} completed in {} ms", joinPoint.getSignature().getName(), duration);
        }
    }

    @AfterThrowing(pointcut = "employeeServiceMethods()", throwing = "ex")
    public void handleExceptions(JoinPoint joinPoint, Throwable ex) {
        logger.error("Exception in method {}: {}", joinPoint.getSignature().getName(), ex.getMessage());
    }
}
