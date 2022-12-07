package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReporterData;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.reponse.CompensationResponse;
import com.mindex.challenge.reponse.EmployeeResponse;
import com.mindex.challenge.service.EmployeeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeServiceImplTest {

    private String employeeUrl;
    private String employeeIdUrl;
    private String reportingStructureUrl;
    private String compensationURL;

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
        compensationURL = "http://localhost:" + port + "/compensation/{id}";
    }

    /**
     * todo add separate unit tests
     * It might be good to break up this into multiple unit tests. We should
     * ideally be testing failure and success against each endpoint separately, but
     * it is also good to do an integration test like this.
     *
     * I'm also wondering if we should be mocking the employeeService here so that we can better control
     * the test and really only excercise the controller. I think a better pattern would be to make a new
     * suite of tests for the controller and unit test only the employeeService with a mocked repository.
     * I wanted to refactor this code and make all these changes but didn't do so in the interest of time.
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
        Employee reportA = createEmployeeWithReports(null);
        Employee reportB = createEmployeeWithReports(null);
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

    @Test
    public void testMultiReportingLayer() {
        Employee reportA = createEmployeeWithReports(null);
        Employee reportB = createEmployeeWithReports(null);
        ArrayList<ReporterData> reportGrandchildren = new ArrayList<>();
        ReporterData dataA = new ReporterData(reportA.getEmployeeId());
        ReporterData dataB = new ReporterData(reportB.getEmployeeId());
        reportGrandchildren.add(dataA);
        reportGrandchildren.add(dataB);

        Employee rightReporter = createEmployeeWithReports(reportGrandchildren);
        Employee leftReporter = createEmployeeWithReports(null);
        ArrayList<ReporterData> reports = new ArrayList<>();
        ReporterData dataRight = new ReporterData(rightReporter.getEmployeeId());
        ReporterData dataLeft = new ReporterData(leftReporter.getEmployeeId());
        reports.add(dataRight);
        reports.add(dataLeft);

        Employee boss = createEmployeeWithReports(reports);

        ReportingStructure multiLayerReportingStructure = restTemplate.getForEntity(reportingStructureUrl,
                ReportingStructure.class,
                boss.getEmployeeId()).getBody();

        assertEmployeeEquivalence(boss, multiLayerReportingStructure.getEmployee());
        assertEquals(4, multiLayerReportingStructure.getNumberOfReports());
    }

    /**
     * We should also test when things crash / break etc. and we should be handling the 500s gracefully
     */
    @Test
    public void testFailureReadingReportingStructure() {
        // this case should not bubble up a 500
        String nonExistentEmployeeId = "random-employee";
        ReportingStructure multiLayerReportingStructure = restTemplate.getForEntity(reportingStructureUrl,
                ReportingStructure.class,
                nonExistentEmployeeId).getBody();

        // I'm actually not sure how to capture this case beacause in PostMan, when I test manually I keep getting a 500 on this
        // should we be using different testing instrumentation? I don't know if I'm fully understaing the restTemplate
        assertNotNull(multiLayerReportingStructure);
    }

    /**
     * Round trip the compenastion.
     */
    @Test
    public void testCompensationCRUD() {
       Employee employee = createEmployeeWithReports(null);

        Double salary = Math.abs(new Random().nextDouble());
        LocalDate date = LocalDate.now();
        Compensation compensation = new Compensation(salary, date);
       // create and read compensation
        CompensationResponse createCompensationResponse = restTemplate.postForEntity(
                compensationURL,
                compensation,
                CompensationResponse.class,
                employee.getEmployeeId()
        ).getBody();


        assertEmployeeEquivalence(new EmployeeResponse(employee), createCompensationResponse.getEmployee());
        assertEquals(salary, createCompensationResponse.getSalary());
        assertEquals(date, createCompensationResponse.getEffectiveDate());



    }

    private Employee createEmployeeWithReports(List<ReporterData> reports) {
        Employee testEmployee = new Employee();
        testEmployee.setEmployeeId(UUID.randomUUID().toString());
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");
        testEmployee.setDirectReports(reports);

        // Create checks
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();

        return createdEmployee;
    }

    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }

    private static void assertEmployeeEquivalence(EmployeeResponse expected, EmployeeResponse actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }
}
