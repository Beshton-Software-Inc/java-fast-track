package com.beshton.shopping.controller;

import com.beshton.shopping.entity.Customer;
import com.beshton.shopping.service.CustomerService;
import com.beshton.shopping.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private S3Service s3Service;

    @Autowired
    private CustomerService customerService;

    @PostMapping("/upload")
    public String uploadCustomerFile(@RequestParam("bucketName") String bucketName,
                                     @RequestParam("key") String key,
                                     @RequestParam("firstName") String firstName,
                                     @RequestParam("lastName") String lastName,
                                     @RequestParam("age") Integer age,
                                     @RequestParam("emailId") String emailId) throws IOException {
        // 创建Customer对象并保存
        Customer customer = new Customer();
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setAge(age);
        customer.setEmailId(emailId);
        customerService.saveCustomer(customer);

        // 生成包含Customer信息的文件
        File customerFile = customerService.generateCustomerFile(customer);

        // 上传文件到S3
        s3Service.uploadFile(bucketName, key, customerFile);

        return "文件上传并客户信息保存成功。";
    }
}
