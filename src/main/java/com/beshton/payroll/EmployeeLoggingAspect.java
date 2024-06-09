package com.beshton.payroll;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class EmployeeLoggingAspect {
    private final Logger logger = LoggerFactory.getLogger(EmployeeLoggingAspect.class);

    @Pointcut("execution(* com.beshton.payroll.EmployeeController.*(..))")
    private void selectAll(){}

    @Before("selectAll()")
    public void beforeAdvice(JoinPoint joinPoint){
        logger.info(String.format("Controller %s is being called. The given arguments are: %s",
                joinPoint.getSignature().toShortString(), Arrays.deepToString(joinPoint.getArgs())));
    }

    @AfterReturning(pointcut = "selectAll()", returning = "retVal")
    public void afterReturningAdvice(JoinPoint joinPoint, Object retVal){
        logger.info(String.format("Controller %s has returned %s",
                joinPoint.getSignature().toShortString(), retVal == null ? "null" : retVal.toString()));
    }

    @After("selectAll()")
    public void afterAdvice(JoinPoint joinPoint){
        logger.info(String.format("Controller %s has finished executing.",
                joinPoint.getSignature().toShortString()));
    }

    @AfterThrowing(pointcut = "selectAll()", throwing = "exception")
    public void AfterThrowingAdvice(JoinPoint joinPoint, Throwable exception){
        logger.warn(String.format("Controller %s has thrown an exception: %s",
                joinPoint.getSignature().toShortString(), exception.getMessage()));
    }
}
