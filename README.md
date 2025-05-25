# Employee Microservice

This is the Employee Microservice in a broader microservices architecture.  
It is responsible for managing employee-related data and actions, including storing employee details and managing their schedules.

---

## Features

- Create, update, delete, and fetch employees
- Handle employee availability
- Integration-ready for communication with other microservices (planned)
- In-memory H2 database support
- Unit and integration tests included

---

## Technologies Used

- Java 14
- Spring Boot
- Spring Web
- Spring Data JPA
- H2 Database (for development/testing)
- JUnit & Mockito for testing
- Maven for build management

---

## Getting Started

### Prerequisites

- Java 14
- Maven 3+

### Run Locally

Clone the repository and navigate to the employee service directory:

```bash
mvn clean install
mvn spring-boot:run
