package com.beshton.shopping.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class PerformanceAspect {

    @Around("execution(* com.beshton.shopping..*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long end = System.currentTimeMillis();

        System.out.println("[Monitor] Method: " + joinPoint.getSignature().getName()
                + " | Class: " + joinPoint.getTarget().getClass().getSimpleName()
                + " | Time: " + (end - start) + " ms");

        return result;
    }
}

