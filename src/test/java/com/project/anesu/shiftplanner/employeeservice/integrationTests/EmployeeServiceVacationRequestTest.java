package com.project.anesu.shiftplanner.employeeservice.integrationTests;

import static com.project.anesu.shiftplanner.employeeservice.controller.EmployeeServiceRestEndpoints.*;
import static org.hamcrest.Matchers.*;

import com.project.anesu.shiftplanner.employeeservice.entity.employee.Employee;
import com.project.anesu.shiftplanner.employeeservice.model.repository.EmployeeRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmployeeServiceVacationRequestTest {

  @LocalServerPort private int port;

  @Autowired EmployeeRepository employeeRepository;

  private Long employeeId;

  @BeforeEach
  void setUp() {
    RestAssured.port = port;

    Employee employee = new Employee();
    employee.setFirstName("Lisa");
    employee.setLastName("Simpson");
    employee.setPhone("123456789");
    employee.setEmail("lisa@gmail.com");
    employee.setPersonUuid(UUID.randomUUID());
    employee.setAddress("Holler street 134, 22525 Hamburg");
    employee.setBirthDate(LocalDate.of(1989, 3, 16));

    Employee savedEmployee = employeeRepository.save(employee);
    employeeId = savedEmployee.getId();
  }

  @Test
  void shouldCreateSuccessfullyCreateVacationRequest() {
    String vacationRequestBody =
        """
                        {
                          "officeLocationId": 1,
                          "startDate": "2025-06-01T08:00:00",
                          "endDate": "2025-06-05T08:00:00",
                          "vacationRequestStatus": "PENDING",
                          "rejectionReason": null,
                   "employee": {
                            "id": %d
                  }
            }
            """
            .formatted(employeeId);

    RestAssured.given()
        .contentType(ContentType.JSON)
        .body(vacationRequestBody)
        .when()
        .post(LANDING_PAGE + CREATE_VACATION_REQUEST)
        .then()
        .statusCode(200)
        .body("status", equalTo("PENDING"))
        .body("officeLocationId", equalTo(1));
  }

  @Test
  void withdrawVacationRequest() {
    String vacationRequestBody =
        """
                            {
                              "officeLocationId": 1,
                              "startDate": "2025-06-01T08:00:00",
                              "endDate": "2025-06-15T08:00:00",
                              "vacationRequestStatus": "PENDING",
                              "rejectionReason": null,
                       "employee": {
                                "id": %d
                      }
                }
                """
            .formatted(employeeId);

    Integer vacationRequestId =
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(vacationRequestBody)
            .when()
            .post(LANDING_PAGE + CREATE_VACATION_REQUEST)
            .then()
            .statusCode(200)
            .body("status", equalTo("PENDING"))
            .body("officeLocationId", equalTo(1))
            .extract()
            .path("id");

    String withdrawVacationRequestBody =
        """
                            {
                              "officeLocationId": 10,
                              "startDate": "2025-06-02T08:00:00",
                              "endDate": "2025-06-13T08:00:00",
                              "vacationRequestStatus": "PENDING",
                              "rejectionReason": null
                            }
                        """;

    RestAssured.given()
        .contentType(ContentType.JSON)
        .body(withdrawVacationRequestBody)
        .when()
        .put(LANDING_PAGE + WITHDRAW_VACATION_REQUEST, employeeId, vacationRequestId)
        .then()
        .statusCode(200)
        .body("status", equalTo("WITHDRAWN"));
  }

  @Test
  void getEmployeeVacationRequests() {
    String vacationRequestOne =
        """
                            {
                              "officeLocationId": 1,
                              "startDate": "2025-06-29T08:00:00",
                              "endDate": "2025-07-09T08:00:00",
                              "vacationRequestStatus": "PENDING",
                              "rejectionReason": null,
                       "employee": {
                                "id": %d
                      }
                }
                """
            .formatted(employeeId);

    RestAssured.given()
        .contentType(ContentType.JSON)
        .body(vacationRequestOne)
        .when()
        .post(LANDING_PAGE + CREATE_VACATION_REQUEST)
        .then()
        .statusCode(200)
        .body("status", equalTo("PENDING"))
        .body("officeLocationId", equalTo(1));

    String vacationRequestTwo =
        """
                            {
                              "officeLocationId": 2,
                              "startDate": "2025-12-26T08:00:00",
                              "endDate": "2026-01-05T08:00:00",
                              "vacationRequestStatus": "PENDING",
                              "rejectionReason": null,
                       "employee": {
                                "id": %d
                      }
                }
                """
            .formatted(employeeId);

    RestAssured.given()
        .contentType(ContentType.JSON)
        .body(vacationRequestTwo)
        .when()
        .post(LANDING_PAGE + CREATE_VACATION_REQUEST)
        .then()
        .statusCode(200)
        .body("status", equalTo("PENDING"))
        .body("officeLocationId", equalTo(2));

    RestAssured.given()
        .pathParam("employeeId", employeeId)
        .contentType(ContentType.JSON)
        .when()
        .get(LANDING_PAGE + GET_VACATION_REQUESTS, employeeId)
        .then()
        .statusCode(200)
        .body("findAll { it.status == 'PENDING' }.size()", greaterThanOrEqualTo(1))
        .body("findAll { it.status == 'PENDING' }.size()", greaterThanOrEqualTo(1));
  }

  @Test
  void getVacationRequestsByGivenDateRange () {
    String vacationRequestBodyOne =
            """
                {
                  "officeLocationId": 1,
                  "startDate": "2025-08-01T08:00:00",
                  "endDate": "2025-08-05T08:00:00",
                  "vacationRequestStatus": "PENDING",
                  "rejectionReason": null,
                   "employee": {
                       "id": %d
                      }
                }
                """
                    .formatted(employeeId);

    RestAssured.given()
            .contentType(ContentType.JSON)
            .body(vacationRequestBodyOne)
            .when()
            .post(LANDING_PAGE + CREATE_VACATION_REQUEST)
            .then()
            .statusCode(200)
            .body("status", equalTo("PENDING"))
            .body("officeLocationId", equalTo(1));


    String vacationRequestBodyTwo =
            """
                  {
                    "officeLocationId": 1,
                    "startDate": "2025-12-28T08:00:00",
                    "endDate": "2026-01-08T08:00:00",
                    "vacationRequestStatus": "PENDING",
                    "rejectionReason": null,
                     "employee": {
                         "id": %d
                      }
                }
                """
                    .formatted(employeeId);

    RestAssured.given()
            .contentType(ContentType.JSON)
            .body(vacationRequestBodyTwo)
            .when()
            .post(LANDING_PAGE + CREATE_VACATION_REQUEST)
            .then()
            .statusCode(200)
            .body("status", equalTo("PENDING"))
            .body("officeLocationId", equalTo(1));

    String startDate = "2025-07-01T08:00:00";
    String endDate = "2026-02-08T08:00:00";

    RestAssured.given()
            .pathParam("employeeId", employeeId)
            .queryParam("startDate", startDate)
            .queryParam("endDate", endDate)
            .contentType(ContentType.JSON)
            .when()
            .get(LANDING_PAGE + VACATION_REQUESTS_DATE_RANGE)
            .then()
            .statusCode(200)
            .body("findAll { it.status == 'PENDING' }.size()", greaterThanOrEqualTo(1))
            .body("findAll { it.status == 'PENDING' }.size()", greaterThanOrEqualTo(1));

  }

}

