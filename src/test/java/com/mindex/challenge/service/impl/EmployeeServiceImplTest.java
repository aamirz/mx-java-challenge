package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReporterData;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.model.CompensationModel;
import com.mindex.challenge.model.EmployeeModel;
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
        EmployeeModel createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, EmployeeModel.class).getBody();

        assertNotNull(createdEmployee.getEmployeeId());
        assertEmployeeEquivalence(new EmployeeModel(testEmployee), createdEmployee);


        // Read checks
        EmployeeModel readEmployee = restTemplate.getForEntity(employeeIdUrl, EmployeeModel.class, createdEmployee.getEmployeeId()).getBody();
        assertEquals(createdEmployee.getEmployeeId(), readEmployee.getEmployeeId());
        assertEmployeeEquivalence(createdEmployee, readEmployee);


        // Update checks
        readEmployee.setPosition("Development Manager");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        EmployeeModel updatedEmployee =
                restTemplate.exchange(employeeIdUrl,
                        HttpMethod.PUT,
                        new HttpEntity<>(readEmployee, headers),
                        EmployeeModel.class,
                        readEmployee.getEmployeeId()).getBody();

        readEmployee = restTemplate.getForEntity(employeeIdUrl, EmployeeModel.class, createdEmployee.getEmployeeId()).getBody();

        assertEmployeeEquivalence(readEmployee, updatedEmployee);
    }

    /**
     * Test case for when the employee has no direct reports (should return zero).
     */
    @Test
    public void testReportingStructureEmpty() {
        // test null case
        EmployeeModel nullDirectReportsEmployee = createEmployeeWithReports(null);

        ReportingStructure nullReportingStructure = restTemplate.getForEntity(reportingStructureUrl,
                ReportingStructure.class,
                nullDirectReportsEmployee.getEmployeeId()).getBody();

        assertEmployeeEquivalence(nullDirectReportsEmployee, nullReportingStructure.getEmployee());
        assertEquals(0, nullReportingStructure.getNumberOfReports());

        // test empty case
        EmployeeModel noDirectReportsEmployee = createEmployeeWithReports(new ArrayList<ReporterData>());

       ReportingStructure emptyReportingStructure = restTemplate.getForEntity(reportingStructureUrl,
               ReportingStructure.class,
               noDirectReportsEmployee.getEmployeeId()).getBody();

       assertEmployeeEquivalence(noDirectReportsEmployee, emptyReportingStructure.getEmployee());
       assertEquals(0, emptyReportingStructure.getNumberOfReports());
    }

    @Test
    public void testSingleReportingLayer() {
        EmployeeModel reportA = createEmployeeWithReports(null);
        EmployeeModel reportB = createEmployeeWithReports(null);
        ArrayList<ReporterData> reports = new ArrayList<>();
        ReporterData dataA = new ReporterData(reportA.getEmployeeId());
        ReporterData dataB = new ReporterData(reportB.getEmployeeId());
        reports.add(dataA);
        reports.add(dataB);

        EmployeeModel employeeWithSingleLayer = createEmployeeWithReports(reports);

        ReportingStructure singleLayerReportingStructure = restTemplate.getForEntity(reportingStructureUrl,
                ReportingStructure.class,
                employeeWithSingleLayer.getEmployeeId()).getBody();

        assertEmployeeEquivalence(employeeWithSingleLayer, singleLayerReportingStructure.getEmployee());
        assertEquals(2, singleLayerReportingStructure.getNumberOfReports());
    }

    @Test
    public void testMultiReportingLayer() {
        EmployeeModel reportA = createEmployeeWithReports(null);
        EmployeeModel reportB = createEmployeeWithReports(null);
        ArrayList<ReporterData> reportGrandchildren = new ArrayList<>();
        ReporterData dataA = new ReporterData(reportA.getEmployeeId());
        ReporterData dataB = new ReporterData(reportB.getEmployeeId());
        reportGrandchildren.add(dataA);
        reportGrandchildren.add(dataB);

        EmployeeModel rightReporter = createEmployeeWithReports(reportGrandchildren);
        EmployeeModel leftReporter = createEmployeeWithReports(null);
        ArrayList<ReporterData> reports = new ArrayList<>();
        ReporterData dataRight = new ReporterData(rightReporter.getEmployeeId());
        ReporterData dataLeft = new ReporterData(leftReporter.getEmployeeId());
        reports.add(dataRight);
        reports.add(dataLeft);

        EmployeeModel boss = createEmployeeWithReports(reports);

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
        EmployeeModel employeeModel = createEmployeeWithReports(null);

        Double salary = Math.abs(new Random().nextDouble());
        LocalDate date = LocalDate.now();
        Compensation compensation = new Compensation(salary, date);
       // create and read compensation
        CompensationModel createCompensationResponse = restTemplate.postForEntity(
                compensationURL,
                compensation,
                CompensationModel.class,
                employeeModel.getEmployeeId()
        ).getBody();


        assertEmployeeEquivalence(employeeModel, createCompensationResponse.getEmployee());
        assertEquals(salary, createCompensationResponse.getSalary());
        assertEquals(date, createCompensationResponse.getEffectiveDate());


        // test reading
        CompensationModel readCompensationResponse = restTemplate.getForEntity(
                compensationURL,
                CompensationModel.class,
                employeeModel.getEmployeeId()
        ).getBody();

        assertEmployeeEquivalence(employeeModel, readCompensationResponse.getEmployee());
        assertEquals(salary, readCompensationResponse.getSalary());
        assertEquals(date, readCompensationResponse.getEffectiveDate());

    }

    private EmployeeModel createEmployeeWithReports(List<ReporterData> reports) {
        EmployeeModel testEmployee = new EmployeeModel();
        testEmployee.setEmployeeId(UUID.randomUUID().toString());
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");
        testEmployee.setDirectReports(reports);

        // Create checks
        EmployeeModel createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, EmployeeModel.class).getBody();

        return createdEmployee;
    }

    private static void assertEmployeeEquivalence(EmployeeModel expected, EmployeeModel actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }
}
