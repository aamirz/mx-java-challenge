package com.mindex.challenge.controller;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.reponse.EmployeeResponse;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class EmployeeController {
    private static final Logger LOG = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/employee")
    public EmployeeResponse create(@RequestBody Employee employee) {
        LOG.debug("Received employee create request for [{}]", employee);

        return new EmployeeResponse(employeeService.createEmployee(employee));
    }

    @GetMapping("/employee/{id}")
    public EmployeeResponse read(@PathVariable String id) {
        LOG.debug("Received employee create request for id [{}]", id);

        return new EmployeeResponse(employeeService.readEmployee(id));
    }

    @PutMapping("/employee/{id}")
    public EmployeeResponse update(@PathVariable String id, @RequestBody Employee employee) {
        LOG.debug("Received employee create request for id [{}] and employee [{}]", id, employee);

        employee.setEmployeeId(id);
        return new EmployeeResponse(employeeService.updateEmployee(employee));
    }

    @GetMapping("/reportingStructure/{id}")
    public ReportingStructure readReportingStructure(@PathVariable String id) {
        LOG.debug("Received reporting structure read request for employee id [{}]", id);

        return employeeService.readReportingStructure(id);
    }
}
