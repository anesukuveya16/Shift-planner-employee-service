# Shift Planner: Employee Service

This microservice is part of an **manager scheduling system** in a healthcare facility, focusing on employee operations in the system.

The **Employee Microservice** focuses on :

- create and send shift and vacation requests to manager 
- Approve or reject shift requests sent from by the manager
- Update schedule after approval from manager
- Delete schedule

It operates **independently** of the Manager Microservice but communicates with it to:

- To recieve feedbcak or updates from the Manager Microserivice
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

- View schedule with specific date range  
- Delete existing schedules 
- Approve or reject shift requests from Manager Service
- create and send vacation requests to Manager Service
- Update employee schedules based on manager actions  
- Get a list of an employee’s shift requests in a specific date range  
- Retrieve planned shift and vacation in specific date range and location  
- Validation layer to enforce business rules (e.g., no overlapping shifts)  

##  Unit tests cover:

Shift request validation

Vacation request validation

Schedule conflict detection

Successful and failing approval flows

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
| Method | Endpoint                                      | Description             |
| --------| -------------------------------------------   | ----------------------- |
| `POST`  | `/schedules`                                  | Create schedule         |
| `PUT`   | `/schedules/{scheduleId}`                    | Update schedule         |
| `GET`   | `/schedules/{scheduleId}/range`              | Get schedules in range  |
| `DELETE`| `/schedules/{scheduleId}`                     | Delete schedule         |



Shift Request
| Method | Endpoint                                                    | Description             |
| ------ | ----------------------------------------------------------- | ----------------------- |
| `PUT`  | `/employees/{employeeId}/shifts`                            | Create shift request    |
| `PUT`  | `/employees/{employeeId}/shifts/{shiftRequestId}/approve`   | Approve shift request   |
| `GET`  | `/shifts/{shiftRequestId}/decline`                          | Reject shift request    |
| `GET`  | `/employees/{employeeId}/shifts`                            | Get shift request  |
| `GET`  | `/employees/{employeeId}/shifts/range`                      | Get shift requests in range  |


Vacation Request
| Method | Endpoint                                                    | Description              |
| ------ | ---------------------------------------------------------   | -----------------------  |
| `POST`  | `/vacations/{vacationRequestId}/approve`                    | Create vacation request |
| `GET`  | `/vacations/{vacationRequestId}/decline`                    | Withdraw vacation request  |
| `GET`  | `/employees/{employeeId}/vacations`                         | Get vacation requests |
| `GET`  | `/employees/{employeeId}/vacations/range`                   | Get vacation requests in range  |
| `GET`  | `/offices/{officeLocationId}/vacations`                     | Get team calendar         |

## ✨ How to run locally:
```bash
Clone project:

git clone https://github.com/yourusername/manager-microservice.git
cd manager-microservice bash

Build project:

./mvnw clean install 

Run the application:

./mvnw spring-boot:run

