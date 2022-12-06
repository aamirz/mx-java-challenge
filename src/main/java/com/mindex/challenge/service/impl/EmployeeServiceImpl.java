package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReporterData;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Employee create(Employee employee) {
        LOG.debug("Creating employee [{}]", employee);

        employee.setEmployeeId(UUID.randomUUID().toString());
        employeeRepository.insert(employee);

        return employee;
    }

    @Override
    public Employee read(String id) {
        LOG.debug("Creating employee with id [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        return employee;
    }

    @Override
    public Employee update(Employee employee) {
        LOG.debug("Updating employee [{}]", employee);

        return employeeRepository.save(employee);
    }

    @Override
    public ReportingStructure getReportingStructure(String id) {
        LOG.debug("Calculating reporting structure for employeeId: [{}]", id);
        Employee employee = read(id);
        int numberOfReports = calculateReports(employee);

        return new ReportingStructure(employee, numberOfReports);
    }

    private int calculateReports(Employee employee) {
        List<ReporterData> directReports = employee.getDirectReports();
        LOG.debug("employee is [{}]", employee);
        if (directReports == null || directReports.size() == 0) {
            return 0;
        } else {
            int subReports = 0;
            for (ReporterData id : directReports) {
                Employee reporter = read(id.getEmployeeId());
                subReports += calculateReports(reporter);
            }
            return subReports + directReports.size();
        }
    }
}
