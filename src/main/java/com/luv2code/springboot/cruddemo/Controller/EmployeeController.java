package com.luv2code.springboot.cruddemo.Controller;

import java.util.List;
import com.luv2code.springboot.cruddemo.entity.Employee;
import com.luv2code.springboot.cruddemo.util.AWSSQSUtil;
import org.springframework.ui.Model;
import com.luv2code.springboot.cruddemo.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    private EmployeeService employeeService;

    @Autowired
    AWSSQSUtil awssqsUtil;

    @Autowired
    public EmployeeController(EmployeeService theEmployeeService){
        employeeService = theEmployeeService;
    }

    @GetMapping("/list")
    public String listEmployees(Model model){
        List<Employee> curList = employeeService.findAll();
        model.addAttribute("employees", curList);
        return "employees/list-employees";
    }

    @GetMapping("/showFormForAdd")
    public String showFormForAdd(Model model){
        model.addAttribute("employee", new Employee());
        return "employees/employee-form";
    }

    @GetMapping("/showFormForUpdate")
    public String showFormForUpdate(@RequestParam("employeeId") int id, Model model){
        Employee cur = employeeService.findById(id);
        model.addAttribute("employee", cur);
        return "employees/employee-form";
    }

    @PostMapping("/save")
    public String saveEmployee(@ModelAttribute("employee") Employee theEmployee){
        employeeService.save(theEmployee);
        String cur = "New Employee created: " + theEmployee.getFirstName() + " " + theEmployee.getLastName();
        String messageId = awssqsUtil.produceMessageToSQS(cur);
        System.out.println("Message pushed to sqs: " + messageId);
        // redirect
        return "redirect:/employees/list";
    }
    @GetMapping("/delete")
    public String deleteEmployee(@RequestParam("employeeId") int id){
        Employee cur = employeeService.findById(id);
        employeeService.deleteById(id);
        String message = "Employee deleted : " + cur.getFirstName() + " " + cur.getLastName();
        String messageId = awssqsUtil.produceMessageToSQS(message);
        System.out.println("Message pushed to sqs: " + messageId);
        return "redirect:/employees/list";
    }
}
