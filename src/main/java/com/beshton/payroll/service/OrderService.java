package com.beshton.payroll.service;

import com.beshton.payroll.exception.OrderNotFoundException;
import com.beshton.payroll.model.Order;
import com.beshton.payroll.model.Status;
import com.beshton.payroll.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public List<Order> findAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Order findOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    @Transactional
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    @Transactional
    public Order updateOrderStatus(Long id, Status status) {
        Order order = findOrderById(id);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    // Simulated method to demonstrate how to handle incoming messages
    // This method would be triggered by a message listener in a real application
    public void handleStatusUpdateMessage(Long orderId, Status newStatus) {
        updateOrderStatus(orderId, newStatus);
    }
}
