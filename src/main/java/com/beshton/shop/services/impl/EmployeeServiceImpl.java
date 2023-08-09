package com.beshton.shop.services.impl;

import com.beshton.shop.advices.*;
import com.beshton.shop.controllers.*;
import com.beshton.shop.entities.*;
import com.beshton.shop.exceptions.*;
import com.beshton.shop.services.*;
import com.beshton.shop.repos.*;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private  EmployeeRepository EmployeeRepository;
    public void setEmployeeRepository(EmployeeRepository repo) {
        this.EmployeeRepository = repo;
    }
    // constructors, other override methods
    @Override
    public Employee getEmployee(String id) {
        Employee emp = null;
        try {
//            emp = EmployeeRepository.findById(emp.getId());
        } catch (Exception ex) {
            emp = null;
        }
        return emp;
    }

    @Override
    public Collection<Employee>  getEmployees() {
        Employee emp = null;
        try {
//            emp = EmployeeRepository.all();
        } catch (Exception ex) {
            emp = null;
        }
        return new ArrayList<Employee>();
    }
    @Override
    public Employee addEmployee(Employee emp) {
//        final Employee existingEmployee = EmployeeRepository.findById(emp.getId());
        Employee existingEmployee = null;
        if (existingEmployee == null) {
//            throw new Exception("Employee is null");
        }

        return existingEmployee;
    }

    // conversion logic
}