package com.beshton.shopping.aspect;

import com.beshton.shopping.entity.Customer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Date;

@Aspect
@Component
public class CustomerMonitoring {

    @Before("execution( * com.beshton.shopping.controller.CustomerController.*(..))")
    public void beforeAdvice(JoinPoint joinPoint){
        System.out.println("Before method invoke:" + joinPoint.getSignature() + "started at" + new Date());
    }
    @After("execution( * com.beshton.shopping.controller.CustomerController.*(..))")
    public void afterAdvice(JoinPoint joinPoint){
        System.out.println("After method invoke:" + joinPoint.getSignature() + "ended at" + new Date());
    }
    @AfterReturning(value = "execution( * com.beshton.shopping.controller.CustomerController.createCustomer(..)) && args(customer,..)")//
    public void afterReturningAdviceForCreateCustomer(JoinPoint joinPoint, Customer customer) {
        if (customer.getAge() < 18) {
            System.out.println("ATTENTION! A teenager customer with age" + customer.getAge() + " was trying to register!" + " Customer ID:"+ customer.getId());
        }
    }
}