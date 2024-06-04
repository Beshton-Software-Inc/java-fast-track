package com.beshton.shopping.service;

import com.beshton.shopping.entity.Customer;
import com.beshton.shopping.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public void saveCustomer(Customer customer) {
        if (customer.getCreatedBy() == null) {
            customer.setCreatedBy("system");
        }
        if (customer.getUpdatedby() == null) {
            customer.setUpdatedby("system");
        }
        customerRepository.save(customer);
    }

    public File generateCustomerFile(Customer customer) throws IOException {
        File file = File.createTempFile("customer", ".txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("ID: " + customer.getId() + "\n");
            writer.write("First Name: " + customer.getFirstName() + "\n");
            writer.write("Last Name: " + customer.getLastName() + "\n");
            writer.write("Age: " + customer.getAge() + "\n");
            writer.write("Email: " + customer.getEmailId() + "\n");
            writer.write("Created At: " + customer.getCreatedAt() + "\n");
            writer.write("Created By: " + customer.getCreatedBy() + "\n");
            writer.write("Updated At: " + customer.getUpdatedAt() + "\n");
            writer.write("Updated By: " + customer.getUpdatedby() + "\n");
        }
        return file;
    }
}
