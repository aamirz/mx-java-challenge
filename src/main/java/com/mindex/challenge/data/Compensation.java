package com.mindex.challenge.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import jdk.vm.ci.meta.Local;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;

public class Compensation {
    private Double salary;

    @DateTimeFormat(pattern = "yyyy-mm-dd")
    private LocalDate effectiveDate;

    public Compensation() {}

    public Compensation(Double salary, LocalDate effectiveDate) {
        this.salary = salary;
        this.effectiveDate = effectiveDate;
    }

    public Double getSalary() { return salary; }

    public LocalDate getEffectiveDate() { return effectiveDate; }

    public void setSalary(Double salary) { this.salary = salary; }

    public void setEffectiveDate(LocalDate effectiveDate) { this.effectiveDate = effectiveDate; }

}
