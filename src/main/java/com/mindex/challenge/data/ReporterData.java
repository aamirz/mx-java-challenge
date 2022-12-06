package com.mindex.challenge.data;

public class ReporterData {
    private String employeeId;

    public ReporterData() {}

    public ReporterData(String id) {
        this.employeeId = id;
    }

    public String getEmployeeId() { return employeeId; }

    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
}
