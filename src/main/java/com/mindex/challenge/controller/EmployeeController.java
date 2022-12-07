package com.mindex.challenge.controller;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.model.CompensationModel;
import com.mindex.challenge.model.EmployeeModel;
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
    public EmployeeModel create(@RequestBody Employee employee) {
        LOG.debug("Received employee create request for [{}]", employee);

        return new EmployeeModel(employeeService.createEmployee(employee));
    }

    @GetMapping("/employee/{id}")
    public EmployeeModel read(@PathVariable String id) {
        LOG.debug("Received employee read request for id [{}]", id);

        return new EmployeeModel(employeeService.readEmployee(id));
    }

    @PutMapping("/employee/{id}")
    public EmployeeModel update(@PathVariable String id, @RequestBody Employee employee) {
        LOG.debug("Received employee update request for id [{}] and employee [{}]", id, employee);

        employee.setEmployeeId(id);
        return new EmployeeModel(employeeService.updateEmployee(employee));
    }

    @GetMapping("/reportingStructure/{id}")
    public ReportingStructure readReportingStructure(@PathVariable String id) {
        LOG.debug("Received reporting structure read request for employee id [{}]", id);

        return employeeService.readReportingStructure(id);
    }

    @GetMapping("/compensation/{id}")
    public CompensationModel readCompensation(@PathVariable String id) {
        LOG.debug("Received compensation read request for employee id [{}]", id);

        return employeeService.readCompensation(id);
    }

    @PostMapping("/compensation/{id}")
    public CompensationModel createCompensation(@PathVariable String id, @RequestBody Compensation compensation) {
        LOG.debug("Received compensation create request for employee id [{}] and compensation [{}]", id, compensation);

        return employeeService.createCompensation(id, compensation);
    }
}
