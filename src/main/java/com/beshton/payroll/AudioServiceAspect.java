package com.beshton.payroll;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AudioServiceAspect {

    private static final Logger logger = LoggerFactory.getLogger(AudioServiceAspect.class);

    private final AudioService audioService;

    public AudioServiceAspect(AudioService audioService) {
        this.audioService = audioService;
    }

    // Define the pointcut separately
    @Pointcut("execution(* com.beshton.payroll.EmployeeController.createEmployee(..)) && args(newEmployee)")
    public void createEmployeePointcut(Employee newEmployee) {}

    // Before advice
    @Before("createEmployeePointcut(newEmployee)")
    public void beforeAdvice(JoinPoint joinPoint, Employee newEmployee) {
        logger.info("Before advice: createEmployee method is about to be called with arguments: {}", newEmployee);
    }

    // AfterReturning advice
    @AfterReturning(pointcut = "createEmployeePointcut(newEmployee)", returning = "result")
    public void afterEmployeeCreated(Employee newEmployee, Employee result) {
        logger.info("AfterReturning advice: createEmployee method executed successfully.");
        String text = "New employee " + newEmployee.getName() + " with role " + newEmployee.getRole() + " has been created.";
        String filename = newEmployee.getName() + ".mp3";
        audioService.generateAndUploadAudio(text, filename);
        logger.info("Audio generation and upload process initiated for employee: {}", newEmployee.getName());
    }

    // After advice
    @After("createEmployeePointcut(newEmployee)")
    public void afterAdvice(JoinPoint joinPoint, Employee newEmployee) {
        logger.info("After advice: createEmployee method has finished execution.");
    }

    // AfterThrowing advice
    @AfterThrowing(pointcut = "createEmployeePointcut(newEmployee)", throwing = "exception")
    public void afterThrowingAdvice(JoinPoint joinPoint, Employee newEmployee, Throwable exception) {
        logger.warn("AfterThrowing advice: createEmployee method threw an exception: {}", exception.getMessage());
    }
}
