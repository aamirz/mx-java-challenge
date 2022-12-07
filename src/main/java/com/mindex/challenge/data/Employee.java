package com.mindex.challenge.data;

import org.springframework.data.annotation.Id;

import java.util.List;

public class Employee {
    @Id
    private String employeeId;
    private String firstName;
    private String lastName;
    private String position;
    private String department;
    // aaz: I refactored the type to be List<ReporterData> so that it would be explicit what data was being stored here
    private List<ReporterData> directReports;
    private Compensation compensation;

    public Employee() {
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

    public Compensation getCompensation() { return compensation; }

    public void setCompensation(Compensation compensation) { this.compensation = compensation; }
}
