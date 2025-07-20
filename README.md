# Shift Planner: Employee Service

This microservice is part of an **manager scheduling system** in a healthcare facility, focusing on employee operations for a shift planner system.

The **Employee Microservice** is responsible for :

- creating and sending shift and vacation requests to manager 
- Approving or rejecting shift requests sent by the manager
- Updating schedules after approval from manager
- Deleting schedules

It operates **independently** of the Manager Microservice but communicates with it to:

- To recieve feedback or updates from the Manager Microserivice
- Maintain consistency across the services

## Tech Stack

- Java (SapMachine 21)  
- Spring Boot
- Spring Data JPA
- H2 Database (in-memory)  
- Maven for build management
- Lombok  
- JUnit 5 and Mockito for testing  
- Rest Assured for integration testing  

##  Key Features

- create and send vacation requests to Manager Service
- View schedule with specific date range  
- Delete existing schedules 
- Approve or reject shift requests from Manager Service
- Update employee schedules based on manager actions  
- Get a list of an employee’s shift requests in a specific date range  
- Retrieve planned shift and vacation in specific date range and location  
- Validation layer to enforce business rules (e.g., no overlapping shifts, should not exceed legal maximum working hours)  

##  Unit tests cover:

Shift request validation

Vacation request validation

Schedule conflict detection

Successful and failing approval flows of business logic

Successful and failing of creating shifts or schedules


##  Integration tests cover:

REST Endpoints functionality


##  Validation rules

Shifts and vacations must not overlap

Vacation days must be valid future dates

Weekly working hours must not be exceeded

Annual vacation days must not be exceeded

Only "pending" shift requests can be approved or rejected


##  REST Endpints

Schedule Request

| Method | Endpoint                                     | Description            |
| --------| ------------------------------------------- | ----------------------- |
| `POST`  | `/{employeeId}/schedule`                    | Create schedule         |
| `PUT`   | `/schedule/{scheduleId}`                    | Update schedule         |
| `GET`   | `/{employeeId}/schedule`                    | Get schedules in range  
| `DELETE`| `/schedule/{scheduleId}`                    | Delete schedule         |


Shift Request

| Method | Endpoint                                                    | Description             |
| ------ | ----------------------------------------------------------- | ----------------------- |
| `POST`  | `/shift-request`                                           | Create shift request    |
| `PUT`  | `/{employeeId}/shift-request/{shiftRequestId}/approve`      | Approve shift request   |
| `GET`  | `/{shiftRequestId}/reject`                                  | Reject shift request    |
| `GET`  | `/{employeeId}/shift-requests`                              | Get shift request  |
| `GET`  | `/{employeeId}/shift-requests/date-range`                   | Get shift requests in range  |


Vacation Request

| Method | Endpoint                                                      | Description              |
| ------ | ---------------------------------------------------------     | -----------------------  |
| `POST`  | `/vacation-request`                                          | Create vacation request  |
| `PUT`  | `/{employeeId}/vacation-request/{vacationRequestId}/withdraw` | Withdraw vacation request  |
| `GET`  | `/employees/{employeeId}/vacations`                           | Get vacation requests    |
| `GET`  | `/{employeeId}/vacation-requests`                             | Get vacation requests    |
| `GET`  | `/{employeeId}/vacation-requests/date-range`                  | Get vacation requests within specific date range  |

## How to run locally:
```bash
Clone project:

git clone https:https://github.com/anesukuveya16/Shift-planner-employee-service
cd manager-microservice bash

Build project:

./mvn clean install 

Run the application:

./mvn spring-boot:run

