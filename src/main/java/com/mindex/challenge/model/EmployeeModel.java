package com.mindex.challenge.model;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReporterData;

import java.util.List;

/**
 * EmployeeModel provides a REST layer abstraction to hide
 * the way we are storing data in mongo with the backing Employee class.
 */
public class EmployeeModel {
    private String employeeId;
    private String firstName;
    private String lastName;
    private String position;
    private String department;
    private List<ReporterData> directReports;

    public EmployeeModel() {
    }

    public EmployeeModel(Employee employee) {
       this.employeeId = employee.getEmployeeId();
       this.firstName = employee.getFirstName();
       this.lastName = employee.getLastName();
       this.position = employee.getPosition();
       this.department = employee.getDepartment();
       this.directReports = employee.getDirectReports();
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public List<ReporterData> getDirectReports() {
        return directReports;
    }

    public void setDirectReports(List<ReporterData> directReports) {
        this.directReports = directReports;
    }
}
