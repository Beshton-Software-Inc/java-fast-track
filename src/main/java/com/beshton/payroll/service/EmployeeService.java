package com.beshton.payroll.service;

import com.beshton.payroll.model.Employee;
import com.beshton.payroll.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final SqsService sqsService;

    @Autowired
    public EmployeeService(EmployeeRepository employeeAccessRepository, SqsService sqsService) {
        this.employeeRepository = employeeAccessRepository;
        this.sqsService = sqsService;
    }

    @Transactional
    public Employee addEmployee(Employee employee) throws IOException {
        Employee savedEmployee = employeeRepository.save(employee);
        String logEntry = "Employee just created: " + employee.toString();
        sqsService.produceMessageToSQS(logEntry);
        return savedEmployee;
    }


    @Transactional(readOnly = true)
    public Employee findEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Employee with id " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Transactional
    public Employee updateEmployee(Long id, Employee newEmployee) {
        return employeeRepository.findById(id)
                .map(employee -> {
                    employee.setFirstName(newEmployee.getFirstName());
                    employee.setLastName(newEmployee.getLastName());
                    employee.setRole(newEmployee.getRole());
                    String logEntry = "Employee just updated: " + employee.toString();
                    sqsService.produceMessageToSQS(logEntry);
                    return employeeRepository.save(employee);
                }).orElseThrow(() -> new IllegalStateException("Employee with id " + id + " not found"));
    }

    @Transactional
    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
        String logEntry = "Employee just deleted!";
        sqsService.produceMessageToSQS(logEntry);

    }
}
