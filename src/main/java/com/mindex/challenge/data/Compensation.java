package com.mindex.challenge.data;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class Compensation {
    private Double salary;

    @JsonFormat(pattern = "mm/dd/yyyy")
    private Date effectiveDate;

    public Compensation() {}

    public Compensation(Double salary, Date effectiveDate) {
        this.salary = salary;
        this.effectiveDate = effectiveDate;
    }

    public Double getSalary() { return salary; }

    public Date getEffectiveDate() { return effectiveDate; }

    public void setSalary(Double salary) { this.salary = salary; }

    public void setEffectiveDate(Date effectiveDate) { this.effectiveDate = effectiveDate; }

}
