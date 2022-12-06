package com.mindex.challenge.service;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.reponse.CompensationResponse;

public interface EmployeeService {
    Employee createEmployee(Employee employee);
    Employee readEmployee(String id);
    Employee updateEmployee(Employee employee);
    ReportingStructure readReportingStructure(String id);
    CompensationResponse createCompensation(String id, Compensation compensation);
    CompensationResponse readCompensation(String id);
}
