package com.beshton.payroll;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Aspect
@Component
public class PerformanceAspect {
    private static final Logger logger = LoggerFactory.getLogger(PerformanceAspect.class);

    // Pointcut to define where the aspect is applied: all methods in the specified package
    @Pointcut("execution(* com.beshton.payroll.EmployeeController.*(..))")
    public void monitoredMethods() {}

    // Around advice that logs method execution time and HTTP status code
    @Around("monitoredMethods()")
    public Object profile(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        Object output = null;

        try {
            output = pjp.proceed(); // proceed with the original method call
        } finally {
            long elapsedTime = System.currentTimeMillis() - start;
            logger.info("Method {} executed in {} ms", pjp.getSignature().toShortString(), elapsedTime);

            // Access the current HTTP request and response
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (requestAttributes != null) {
                HttpServletResponse response = (HttpServletResponse) requestAttributes.getResponse();
                if (response != null) {
                    int statusCode = response.getStatus();
                    logger.info("Method {} returned HTTP status code {}", pjp.getSignature().toShortString(), statusCode);
                }
            }
        }

        return output;
    }
}
