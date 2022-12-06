package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReporterData;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeServiceImplTest {

    private String employeeUrl;
    private String employeeIdUrl;
    private String reportingStructureUrl;

    @Autowired
    private EmployeeService employeeService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        employeeIdUrl = "http://localhost:" + port + "/employee/{id}";
        reportingStructureUrl = "http://localhost:" + port + "/reportingStructure/{id}";
    }

    /**
     * todo add separate unit tests
     * It might be good to break up this into multiple unit tests. We should
     * ideally be testing failure and success against each endpoint separately, but
     * it is also good to do an integration test like this.
     */
    @Test
    public void testCreateReadUpdate() {
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");

        // Create checks
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();

        assertNotNull(createdEmployee.getEmployeeId());
        assertEmployeeEquivalence(testEmployee, createdEmployee);


        // Read checks
        Employee readEmployee = restTemplate.getForEntity(employeeIdUrl, Employee.class, createdEmployee.getEmployeeId()).getBody();
        assertEquals(createdEmployee.getEmployeeId(), readEmployee.getEmployeeId());
        assertEmployeeEquivalence(createdEmployee, readEmployee);


        // Update checks
        readEmployee.setPosition("Development Manager");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Employee updatedEmployee =
                restTemplate.exchange(employeeIdUrl,
                        HttpMethod.PUT,
                        new HttpEntity<Employee>(readEmployee, headers),
                        Employee.class,
                        readEmployee.getEmployeeId()).getBody();

        readEmployee = restTemplate.getForEntity(employeeIdUrl, Employee.class, createdEmployee.getEmployeeId()).getBody();

        assertEmployeeEquivalence(readEmployee, updatedEmployee);
    }

    /**
     * Test case for when the employee has no direct reports (should return zero).
     */
    @Test
    public void testReportingStructureEmpty() {
        // test null case
        Employee nullDirectReportsEmployee = createEmployeeWithReports(null);

        ReportingStructure nullReportingStructure = restTemplate.getForEntity(reportingStructureUrl,
                ReportingStructure.class,
                nullDirectReportsEmployee.getEmployeeId()).getBody();

        assertEmployeeEquivalence(nullDirectReportsEmployee, nullReportingStructure.getEmployee());
        assertEquals(0, nullReportingStructure.getNumberOfReports());

        // test empty case
       Employee noDirectReportsEmployee = createEmployeeWithReports(new ArrayList<ReporterData>());

       ReportingStructure emptyReportingStructure = restTemplate.getForEntity(reportingStructureUrl,
               ReportingStructure.class,
               noDirectReportsEmployee.getEmployeeId()).getBody();

       assertEmployeeEquivalence(noDirectReportsEmployee, emptyReportingStructure.getEmployee());
       assertEquals(0, emptyReportingStructure.getNumberOfReports());
    }

    @Test
    public void testSingleReportingLayer() {
        Employee reportA = createRandomEmployee();
        Employee reportB = createRandomEmployee();
        ArrayList<ReporterData> reports = new ArrayList<>();
        ReporterData dataA = new ReporterData(reportA.getEmployeeId());
        ReporterData dataB = new ReporterData(reportB.getEmployeeId());
        reports.add(dataA);
        reports.add(dataB);

        Employee employeeWithSingleLayer = createEmployeeWithReports(reports);

        ReportingStructure singleLayerReportingStructure = restTemplate.getForEntity(reportingStructureUrl,
                ReportingStructure.class,
                employeeWithSingleLayer.getEmployeeId()).getBody();

        assertEmployeeEquivalence(employeeWithSingleLayer, singleLayerReportingStructure.getEmployee());
        assertEquals(2, singleLayerReportingStructure.getNumberOfReports());

    }

    private Employee createEmployeeWithReports(List<ReporterData> reports) {
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");
        testEmployee.setDirectReports(reports);

        // Create checks
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();

        return createdEmployee;
    }

    private Employee createRandomEmployee() {
        Employee testEmployee = new Employee();
        testEmployee.setEmployeeId(UUID.randomUUID().toString());
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");

        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();

        return createdEmployee;

    }

    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }
}
