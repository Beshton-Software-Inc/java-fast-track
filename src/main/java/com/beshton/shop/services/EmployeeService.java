package com.beshton.shop.services;

import com.beshton.shop.advices.*;
import com.beshton.shop.controllers.*;
import com.beshton.shop.entities.*;
import com.beshton.shop.exceptions.*;
import com.beshton.shop.repos.*;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.*;

@Service
public interface EmployeeService {
    Collection<Employee> getEmployees();
    Employee addEmployee(Employee employee);
    Employee getEmployee(String id);
}