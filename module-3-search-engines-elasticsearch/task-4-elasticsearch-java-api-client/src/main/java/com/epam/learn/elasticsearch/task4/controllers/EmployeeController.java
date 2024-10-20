package com.epam.learn.elasticsearch.task4.controllers;

import com.epam.learn.elasticsearch.task4.services.EmployeeService;
import com.epam.learn.elasticsearch.task4.models.Employee;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public List<Employee> getAllEmployees() throws IOException {
        return employeeService.getAllEmployees();
    }

    @GetMapping("/{id}")
    public Employee getEmployeeById(@PathVariable String id) throws IOException {
        return employeeService.getEmployeeById(id);
    }

    @PostMapping("/{id}")
    public void createEmployee(@PathVariable String id, @RequestBody Employee employee) throws IOException {
        employeeService.createEmployee(id, employee);
    }

    @DeleteMapping("/{id}")
    public void deleteEmployee(@PathVariable String id) throws IOException {
        employeeService.deleteEmployee(id);
    }

    @GetMapping("/search")
    public List<Employee> searchEmployees(@RequestParam String field, @RequestParam String value) throws IOException {
        return employeeService.searchEmployees(field, value);
    }

    @GetMapping("/aggregate")
    public double aggregateEmployeesByField(@RequestParam String field, @RequestParam String metricType) throws IOException {
        return employeeService.aggregateEmployeesByField(field, metricType);
    }

}
