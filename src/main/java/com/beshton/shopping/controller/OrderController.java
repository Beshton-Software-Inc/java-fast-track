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
import java.util.Optional;

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
        if (order.getStatus() == null) {
            // Set it to the default value "PENDING"
            order.setStatus(Order.OrderStatus.PENDING);
        }
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
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id : " + orderId));
        return ResponseEntity.ok(order);
    }

    // Get orders by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable("status") String status) {
        try {
            Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
            List<Order> orders = orderRepository.findByStatus(orderStatus);
            return ResponseEntity.ok(orders);
        } catch (IllegalArgumentException e) {
            // Handle invalid status here, e.g., return a 400 Bad Request response
            return ResponseEntity.badRequest().build();
        }
    }

    // UPDATE order
    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@Valid @RequestBody Order order, @PathVariable ("id") long orderId) throws OrderNotFoundException {
        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id :" + orderId));
        existingOrder.setCustomerName(order.getCustomerName());
        existingOrder.setProductID(order.getProductID());
        existingOrder.setQuantity(order.getQuantity());
        existingOrder.setPrice(order.getPrice());
        existingOrder.setShippingAddress(order.getShippingAddress());
        Order updatedOrder = orderRepository.save(existingOrder);
        return ResponseEntity.ok(updatedOrder);
    } // Status not updated here


    // Update order status
    @PutMapping("/{id}/status")
    public ResponseEntity<String> updateOrderStatus(
            @PathVariable("id") Long orderId,
            @RequestBody Map<String, String> requestBody
    ) {
        String newStatus = requestBody.get("status");

        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();

            // Validate and update the order status using the enum
            try {
                Order.OrderStatus statusEnum = Order.OrderStatus.valueOf(newStatus);
                order.setStatus(statusEnum);
                orderRepository.save(order);
                return ResponseEntity.ok("Order status updated successfully.");
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Invalid order status.");
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Cancel Order (one-time action)
//    @PutMapping("/{id}/cancel")
//    public ResponseEntity<String> cancelOrder(@PathVariable("id") Long orderId) {
//        Optional<Order> optionalOrder = orderRepository.findById(orderId);
//        if (optionalOrder.isPresent()) {
//            Order order = optionalOrder.get();
//            if (order.getStatus() != Order.OrderStatus.CANCELLED) {
//                order.setStatus(Order.OrderStatus.CANCELLED);
//                orderRepository.save(order);
//                return ResponseEntity.ok("Order cancelled successfully.");
//            } else {
//                return ResponseEntity.badRequest().body("Order is already cancelled.");
//            }
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }

    // Cancel Order (one-time action)
    @PutMapping("/{id}/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable("id") Long orderId) throws OrderNotFoundException {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            if (order.getStatus() != Order.OrderStatus.CANCELLED) {
                order.setStatus(Order.OrderStatus.CANCELLED);
                orderRepository.save(order);
                return ResponseEntity.ok("Order cancelled successfully.");
            } else {
                return ResponseEntity.badRequest().body("Order is already cancelled.");
            }
        } else {
            throw new OrderNotFoundException("Order not found with id: " + orderId);
        }
    }



    // DELETE order by id
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteOrder(
            @PathVariable(value = "id") Long productId) throws OrderNotFoundException {
        Order order = orderRepository.findById(productId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + productId));
        orderRepository.delete(order);
        return ResponseEntity.ok(Collections.singletonMap("deleted", Boolean.TRUE));
    }

}
