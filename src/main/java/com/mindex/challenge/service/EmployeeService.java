package com.mindex.challenge.service;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.model.CompensationModel;

public interface EmployeeService {
    Employee createEmployee(Employee employee);
    Employee readEmployee(String id);
    Employee updateEmployee(Employee employee);
    ReportingStructure readReportingStructure(String id);
    CompensationModel createCompensation(String id, Compensation compensation);
    CompensationModel readCompensation(String id);
}
