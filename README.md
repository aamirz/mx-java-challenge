# Coding Challenge
## What's Provided
A simple [Spring Boot](https://projects.spring.io/spring-boot/) web application has been created and bootstrapped 
with data. The application contains information about all employees at a company. On application start-up, an in-memory 
Mongo database is bootstrapped with a serialized snapshot of the database. While the application runs, the data may be
accessed and mutated in the database without impacting the snapshot.

### How to Run
The application may be executed by running `gradlew bootRun`.

### How to Use
The following endpoints are available to use:
```
* CREATE
    * HTTP Method: POST 
    * URL: localhost:8080/employee
    * PAYLOAD: Employee
    * RESPONSE: Employee
* READ
    * HTTP Method: GET 
    * URL: localhost:8080/employee/{id}
    * RESPONSE: Employee
* UPDATE
    * HTTP Method: PUT 
    * URL: localhost:8080/employee/{id}
    * PAYLOAD: Employee
    * RESPONSE: Employee
```
The Employee has a JSON schema of:
```json
{
  "type":"Employee",
  "properties": {
    "employeeId": {
      "type": "string"
    },
    "firstName": {
      "type": "string"
    },
    "lastName": {
          "type": "string"
    },
    "position": {
          "type": "string"
    },
    "department": {
          "type": "string"
    },
    "directReports": {
      "type": "array",
      "items" : "string"
    }
  }
}
```
For all endpoints that require an "id" in the URL, this is the "employeeId" field.

## What to Implement
Clone or download the repository, do not fork it.

### Task 1
Create a new type, ReportingStructure, that has two properties: employee and numberOfReports.

For the field "numberOfReports", this should equal the total number of reports under a given employee. The number of 
reports is determined to be the number of directReports for an employee and all of their distinct reports. For example, 
given the following employee structure:
```
                    John Lennon
                /               \
         Paul McCartney         Ringo Starr
                               /        \
                          Pete Best     George Harrison
```
The numberOfReports for employee John Lennon (employeeId: 16a596ae-edd3-4847-99fe-c4518e82c86f) would be equal to 4. 

This new type should have a new REST endpoint created for it. This new endpoint should accept an employeeId and return 
the fully filled out ReportingStructure for the specified employeeId. The values should be computed on the fly and will 
not be persisted.

### Solution 1

For task 1, I implemented a get endpoint with the following signature: 

```
* READ
    * HTTP Method: GET 
    * URL: localhost:8080/reportingStructure/{id}
    * RESPONSE: ReportingStructure 
    * NOTE: id is a String employee id
```

Where ReportingStructure looks like this:

```json
{
  "type": "ReportingStructure",
  "employeeId": {
    "type": "string"
  },
  "numberOfReports": {
    "type": "int"
  }
}

```

As I wrote code and learned more about Spring Boot I realized I could have done some good refactoring. Here are some 
refactors I would do if I had more time: 

- separate all the response / request models from the data model classes we are storing in Mongo. I'm still not satisfied with how these classes are structured. 
- make it so that there is a separate test sutie for the Controller that exercises REST failure cases 
- make it so that EmployeeServiceImpl has unit tests against each unit within the service interface and have the tests key off the interface (so we get free tests for each implmenting class)
  - test failure cases in EmployeeServiceImpl with the EmployeeRepository mocked for success / failure

^ with the current testing infrastructure we really have integration tests and we need a better separation of 
responsibilities to make these pure unit tests. 

### Task 2
Create a new type, Compensation. A Compensation has the following fields: employee, salary, and effectiveDate. Create 
two new Compensation REST endpoints. One to create and one to read by employeeId. These should persist and query the 
Compensation from the persistence layer.

### Solution 2

For Task 2 I implemented create and read enpoints with the following signatures: 

```
* CREATE
    * HTTP Method: POST 
    * URL: localhost:8080/compensation/{id}
    * PAYLOAD: Compensation
    * RESPONSE: CompensationModel 
* READ
    * HTTP Method: GET 
    * URL: localhost:8080/compensation/{id}
    * RESPONSE: CompensationModel 
```

Where Compensation is specified by: 

```json
{
  "type": "Compensation",
  "compensation": {
    "type": "double"
  },
  "effectiveDate": {
    "type": "LocalDateTime",
    "format": "yyyy-mm-dd"
  }
}
```

and CompensationModel is specified by: 

```json
{
  "type": "CompensationModel",
  "employee": {
    "type": "EmployeeModel"
  },
  "compensation": {
    "type": "double"
  },
  "effectiveDate": {
    "type": "LocalDateTime",
    "format": "yyyy-mm-dd"
  }
}
```

In this solution I did a little refactoring to separate types we were responding with from types used to represent 
persisted data. It seemed like since we are using Mongo, the optimal solution would be to nest compensation on the 
Employee itself, since it is information who's lifecycle is a subset of the Employee's lifecycle. I still would like 
to refactor this code so that there is a better separation here and not so many copied fields...


## Delivery
Please upload your results to a publicly accessible Git repo. Free ones are provided by Github and Bitbucket.
