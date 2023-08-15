package com.beshton.shopping.controller;


import com.beshton.shopping.entity.Order;
import com.beshton.shopping.exception.OrderNotFoundException;
import com.beshton.shopping.repository.OrderRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderRepository orderRepository;
    @Autowired
    public OrderController(OrderRepository orderRepository){
        this.orderRepository = orderRepository;
    }

    // CREATE order
    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody Order order) {
        Order savedOrder  = orderRepository.save(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);
    }


    // READ
    // get all orders
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderRepository.findAll());
    }

    // get order by id
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(
            @PathVariable(value = "id") Long orderId) throws OrderNotFoundException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Product not found on :: " + orderId));
        return ResponseEntity.ok(order);
    }
    // UPDATE order
    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@Valid @RequestBody Order order, @PathVariable ("id") long orderId) throws OrderNotFoundException {
        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id :" + orderId));
        existingOrder.setCustomerName(order.getCustomerName());
//        existingOrder.setOrderTime(LocalDateTime.now());
        existingOrder.setProductID(order.getProductID());
        existingOrder.setQuantity(order.getQuantity());
        existingOrder.setPrice(order.getPrice());
        existingOrder.setShippingAddress(order.getShippingAddress());
//        existingOrder.setPaymentMethod(order.getPaymentMethod());
        Order updatedOrder = orderRepository.save(existingOrder);
        return ResponseEntity.ok(updatedOrder);
    }

    // DELETE order by id
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteOrder(
            @PathVariable(value = "id") Long productId) throws OrderNotFoundException {
        Order order = orderRepository.findById(productId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found on :: " + productId));
        orderRepository.delete(order);
        return ResponseEntity.ok(Collections.singletonMap("deleted", Boolean.TRUE));
    }

}
