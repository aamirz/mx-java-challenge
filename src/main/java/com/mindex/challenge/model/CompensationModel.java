package com.mindex.challenge.model;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * CompensationModel provides a REST layer abstraction to hide
 * the backing Compensation data class.
 */
public class CompensationModel {
    private EmployeeModel employee;
    private Double salary;

    @DateTimeFormat(pattern = "yyyy-dd-mm")
    private LocalDate effectiveDate;

    public CompensationModel() {}

    public CompensationModel(Compensation compensation, Employee employee) {
        this.employee = new EmployeeModel(employee);
        this.salary = compensation.getSalary();
        this.effectiveDate = compensation.getEffectiveDate();
    }

    public CompensationModel(Double salary, LocalDate effectiveDate, EmployeeModel employee) {
        this.salary = salary;
        this.effectiveDate = effectiveDate;
        this.employee = employee;
    }

    public Double getSalary() { return salary; }

    public void setSalary(Double salary) { this.salary = salary; }

    public LocalDate getEffectiveDate() { return effectiveDate; }

    public void setEffectiveDate(LocalDate effectiveDate) { this.effectiveDate = effectiveDate; }

    public EmployeeModel getEmployee() { return employee; }

    public void setEmployee(EmployeeModel employee) { this.employee = employee; }
}
