package com.mindex.challenge.reponse;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * CompensationModel provides a REST layer abstraction to hide
 * the backing Compensation data class.
 */
public class CompensationResponse {
    private EmployeeResponse employee;
    private Double salary;

    @DateTimeFormat(pattern = "yyyy-dd-mm")
    private LocalDate effectiveDate;

    public CompensationResponse() {}

    public CompensationResponse(Compensation compensation, Employee employee) {
        this.employee = new EmployeeResponse(employee);
        this.salary = compensation.getSalary();
        this.effectiveDate = compensation.getEffectiveDate();
    }

    public CompensationResponse(Double salary, LocalDate effectiveDate, EmployeeResponse employee) {
        this.salary = salary;
        this.effectiveDate = effectiveDate;
        this.employee = employee;
    }

    public Double getSalary() { return salary; }

    public void setSalary(Double salary) { this.salary = salary; }

    public LocalDate getEffectiveDate() { return effectiveDate; }

    public void setEffectiveDate(LocalDate effectiveDate) { this.effectiveDate = effectiveDate; }

    public EmployeeResponse getEmployee() { return employee; }

    public void setEmployee(EmployeeResponse employee) { this.employee = employee; }
}
