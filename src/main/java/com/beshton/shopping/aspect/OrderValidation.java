package com.beshton.shopping.aspect;


import com.beshton.shopping.entity.Order;
import com.beshton.shopping.exception.ValidationException;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Aspect
@Component
public class OrderValidation {

    @Pointcut("execution(* com.beshton.shopping.controller.OrderController.createOrder(..))")
    private void createOrderAdvice() {}

    @Pointcut("execution(* com.beshton.shopping.controller.OrderController.updateOrder(..))")
    private void updateOrderAdvice() {}

    @Before("createOrderAdvice() || updateOrderAdvice()")
    public void validateBeforeOrderOperation(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

            for (Object arg : args) {
                if (arg instanceof Order order) {

                    if (order.getCustomerName() == null || order.getCustomerName().trim().isEmpty()) {
                        throw new ValidationException("Customer name is required.");
                    }
                    if (order.getProductID() == null|| order.getProductID().trim().isEmpty()) {
                        throw new ValidationException("Invalid product ID.");
                    }

                    if (order.getQuantity() <= 0) {
                        throw new ValidationException("Invalid order quantity.");
                    }

                    if (order.getPrice() == null || order.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                        throw new ValidationException("Invalid price.");
                    }

                    if (order.getShippingAddress() == null || order.getShippingAddress().trim().isEmpty()) {
                        throw new ValidationException("Shipping address is required.");
                    }

                }
            }
        }
    }


