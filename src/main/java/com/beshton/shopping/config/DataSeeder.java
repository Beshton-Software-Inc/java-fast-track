package com.beshton.shopping.config;

import com.beshton.shopping.entity.Order;
import com.beshton.shopping.repository.OrderRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;

@Component
public class DataSeeder {

    private final OrderRepository orderRepository;

    @Autowired
    public DataSeeder(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
    @PostConstruct
    public void seedData() {
        // Create and insert pre-existing orders here
        Order order1 = new Order()
                .setCustomerName("Jeff")
                .setProductID("X2333")
                .setQuantity(500)
                .setPrice(new BigDecimal("123.44"))
                .setShippingAddress("123 Where St")
                .setStatus(Order.OrderStatus.PENDING);

        Order order2 = new Order()
                .setCustomerName("Jackson")
                .setProductID("Y666")
                .setQuantity(700)
                .setPrice(new BigDecimal("566.77"))
                .setShippingAddress("258 What Ave")
                .setStatus(Order.OrderStatus.SHIPPED);
        // Save orders to the database
        orderRepository.saveAll(Arrays.asList(order1, order2));
    }
}
