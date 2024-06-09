package com.beshton.payroll;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeRepository repository;

    @MockBean
    private EmployeeModelAssembler assembler;

    public static String asJsonString(final Object obj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getAllEmployeesTest() throws Exception {
        // Mock employees
        List<Employee> employees = Arrays.asList(new Employee("Alice", "Developer"), new Employee("Bob", "Designer"));
        given(repository.findAll()).willReturn(employees);

        // Mock assembler behavior
        employees.forEach(employee ->
                given(assembler.toModel(employee)).willReturn(EntityModel.of(employee))
        );

        // Perform request and assertions
        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.employeeList", hasSize(employees.size())));
    }

    @Test
    void getSingleEmployeeTest() throws Exception {
        Long employeeId = 1L;
        Employee employee = new Employee("David", "Analyst");

        // Mock repository to return the employee when findById is called
        given(repository.findById(employeeId)).willReturn(Optional.of(employee));

        // Mock assembler to return an EntityModel of the employee
        given(assembler.toModel(employee)).willReturn(EntityModel.of(employee));

        // Perform the GET request and assert the response
        mockMvc.perform(get("/employees/{id}", employeeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(employee.getName())))
                .andExpect(jsonPath("$.role", is(employee.getRole())));
    }

    @Test
    void postNewEmployeeTest() throws Exception {
        Employee newEmployee = new Employee("Charlie", "Manager");

        // Mock repository to return the new employee when save is called
        given(repository.save(any(Employee.class))).willReturn(newEmployee);

        // Perform the POST request and assert the response
        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(newEmployee)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(newEmployee.getName())))
                .andExpect(jsonPath("$.role", is(newEmployee.getRole())));
    }

    @Test
    void updateEmployeeTest() throws Exception {
        Long employeeId = 1L;
        Employee existingEmployee = new Employee("David", "Analyst");
        Employee updatedEmployee = new Employee("David Updated", "Senior Analyst");

        // Mock repository to return existing employee for findById
        given(repository.findById(employeeId)).willReturn(Optional.of(existingEmployee));

        // Mock repository to return the updated employee when save is called
        given(repository.save(any(Employee.class))).willReturn(updatedEmployee);

        // Perform the PUT request and assert the response
        mockMvc.perform(put("/employees/{id}", employeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updatedEmployee)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(updatedEmployee.getName())))
                .andExpect(jsonPath("$.role", is(updatedEmployee.getRole())));
    }

    @Test
    void deleteEmployeeTest() throws Exception {
        Long employeeId = 1L;

        // Perform the DELETE request and assert the response
        mockMvc.perform(delete("/employees/{id}", employeeId))
                .andExpect(status().isOk());

        // Optionally, verify that the repository's deleteById method was called
        verify(repository, times(1)).deleteById(employeeId);
    }
}

