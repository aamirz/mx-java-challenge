package com.mindex.challenge.data;

import com.mindex.challenge.model.EmployeeModel;

public class ReportingStructure {
    private EmployeeModel employee;
    private int numberOfReports;

    public ReportingStructure() {}

    public ReportingStructure(EmployeeModel employee, int numberOfReports) {
        this.employee = employee;
        this.numberOfReports = numberOfReports;
    }

    public EmployeeModel getEmployee() { return employee; }

    public int getNumberOfReports() { return  numberOfReports; }

    public void setEmployee(EmployeeModel employee) { this.employee = employee; }

    public void setNumberOfReports(int numberOfReports) { this.numberOfReports = numberOfReports; }

}
