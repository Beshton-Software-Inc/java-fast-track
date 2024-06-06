package com.beshton.shopping.controller;

import com.beshton.shopping.entity.Customer;
import com.beshton.shopping.exception.CustomerNotFoundException;
import com.beshton.shopping.service.CustomerService;
import com.beshton.shopping.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private S3Service s3Service;
    @Autowired
    private CustomerService customerService;
    // upload
    @PostMapping("/upload")
    public String uploadCustomerFile(@RequestParam("bucketName") String bucketName,
                                     @RequestParam("key") String key,
                                     @RequestParam("firstName") String firstName,
                                     @RequestParam("lastName") String lastName,
                                     @RequestParam("age") Integer age,
                                     @RequestParam("emailId") String emailId) throws IOException {
        // creare Customer and save
        Customer customer = new Customer();
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setAge(age);
        customer.setEmailId(emailId);
        customerService.saveCustomer(customer);

        // create Customer file
        File customerFile = customerService.generateCustomerFile(customer);

        // upload to S3
        s3Service.uploadFile(bucketName, key, customerFile);

        return "Successfully uploaded customer";
    }

    // delete
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<String> deleteCustomerFile(@PathVariable Long id,
                                                     @RequestParam("bucketName") String bucketName,
                                                     @RequestParam("key") String key) throws CustomerNotFoundException {
        Customer customer = customerService.getCustomerById(id);
        if (customer == null) {
            throw new CustomerNotFoundException("Customer not found on :: " + id);
        }

        // delete from S3
        s3Service.deleteFile(bucketName, key);
        // Delete customer from database
        customerService.deleteCustomerById(id);

        return ResponseEntity.ok("Successfully deleted customer file from the S3.");
    }

    // Search customers (for example by email or name)
    @GetMapping("/search")
    public ResponseEntity<List<Customer>> searchCustomers(@RequestParam String query) {
        List<Customer> customers = customerService.searchCustomers(query);
        return ResponseEntity.ok(customers);
    }

    // Get a single customer by ID
    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) throws CustomerNotFoundException {
        Customer customer = customerService.getCustomerById(id);
        if (customer == null) {
            throw new CustomerNotFoundException("Customer not found on :: " + id);
        }
        return ResponseEntity.ok(customer);
    }

    // Get all customers
    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }


}
