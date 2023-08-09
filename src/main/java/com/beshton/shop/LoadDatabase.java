package com.beshton.shop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.beshton.shop.exceptions.*;
import com.beshton.shop.entities.*;
import com.beshton.shop.controllers.*;
import com.beshton.shop.advices.*;
import com.beshton.shop.repos.*;


@Configuration
class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);
//
//    @Bean
//    CommandLineRunner initDatabase(EmployeeRepository repository) {
//
//        return args -> {
//            log.info("Preloading " + repository.save(new Employee("Bilbo Baggins", "burglar")));
//            log.info("Preloading " + repository.save(new Employee("Frodo Baggins", "thief")));
//        };
//    }

    @Bean
    CommandLineRunner initDatabase(EmployeeRepository employeeRepository, OrderRepository orderRepository) {

        return args -> {
//            employeeRepository.save(new Employee("Bilbo", "Baggins", "burglar"));
//            employeeRepository.save(new Employee("Frodo", "Baggins", "thief"));

            employeeRepository.findAll().forEach(employee -> log.info("Preloaded " + employee));

//
//            orderRepository.save(new CustomerOrder("MacEmployee Pro", Status.COMPLETED));
//            orderRepository.save(new CustomerOrder("iPhone", Status.IN_PROGRESS));

            orderRepository.findAll().forEach(order -> {
                log.info("Preloaded " + order);
            });

        };
    }
}