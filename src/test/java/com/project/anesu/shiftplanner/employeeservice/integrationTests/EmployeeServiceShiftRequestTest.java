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
class EmployeeServiceShiftRequestTest {

  @LocalServerPort private int port;

  @Autowired EmployeeRepository employeeRepository;

  private Long employeeId;

  @BeforeEach
  void setUp() {
    RestAssured.port = port;

    Employee employee = new Employee();

    employee.setFirstName("Bart");
    employee.setLastName("Simpson");
    employee.setPhone("0123456789");
    employee.setBirthDate(LocalDate.of(1989, 3, 16));
    employee.setEmail("bart@gmail.com");
    employee.setPersonUuid(UUID.randomUUID());
    employee.setAddress("Holler street 134, 22525 Hamburg");

    Employee savedEmployee = employeeRepository.save(employee);
    employeeId = savedEmployee.getId();
  }

  @Test
  void shouldCreateShiftRequest() {
    String shiftRequestBody =
        """
                          {
                          "shiftDate": "2025-06-20T08:00:00",
                          "shiftLengthInHours": 8,
                          "shiftType": "MORNING_SHIFT",
                          "shiftRequestStatus": "PENDING",
                          "rejectionReason": null,
                        "employee": {
                            "id": %d
                  }
            }
            """
            .formatted(employeeId);

    RestAssured.given()
        .contentType(ContentType.JSON)
        .body(shiftRequestBody)
        .when()
        .post(LANDING_PAGE + CREATE_SHIFT_REQUEST)
        .then()
        .statusCode(200)
        .body("status", equalTo("PENDING"))
        .body("shiftType", equalTo("MORNING_SHIFT"))
        .body("shiftLengthInHours", equalTo(8));
  }

  @Test
  void shouldApproveShiftRequestSuccessfully() {

    String shiftRequestBody =
        """
                          {
                          "shiftDate": "2025-06-20T08:00:00",
                          "shiftLengthInHours": 8,
                          "shiftType": "MORNING_SHIFT",
                          "shiftRequestStatus": "PENDING",
                          "rejectionReason": null,
                          "employee": {
                          "id": %d
                          }
                        }
            """
            .formatted(employeeId);

    Integer shiftRequestId =
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(shiftRequestBody)
            .when()
            .post(LANDING_PAGE + CREATE_SHIFT_REQUEST)
            .then()
            .statusCode(200)
            .body("status", equalTo("PENDING"))
            .body("shiftType", equalTo("MORNING_SHIFT"))
            .body("shiftLengthInHours", equalTo(8))
            .extract()
            .path("id");

    String approveShiftRequestBody =
        """
                          {
                          "shiftDate": "2025-06-20T8:00:00",
                          "shiftLengthInHours": 8,
                          "shiftType": "MORNING_SHIFT",
                          "shiftRequestStatus": "APPROVED"
                          "rejectionReason": null
                        }
                    """;

    RestAssured.given()
        .contentType(ContentType.JSON)
        .body(approveShiftRequestBody)
        .when()
        .put(LANDING_PAGE + APPROVE_SHIFT_REQUEST, shiftRequestId, employeeId)
        .then()
        .statusCode(200)
        .body("status", equalTo("APPROVED"))
        .body("shiftType", equalTo("MORNING_SHIFT"))
        .body("shiftLengthInHours", equalTo(8));
  }

  @Test
  void shouldRejectShiftRequest() {
    String shiftRequestBody =
        """
                          {
                          "shiftDate": "2025-06-20T21:30:00",
                          "shiftLengthInHours": 8,
                          "shiftType": "NIGHT_SHIFT",
                          "shiftRequestStatus": "PENDING",
                          "rejectionReason": null,
                         "employee": {
                            "id": %d
                  }
            }
            """
            .formatted(employeeId);

    Integer shiftRequestId =
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(shiftRequestBody)
            .when()
            .post(LANDING_PAGE + CREATE_SHIFT_REQUEST)
            .then()
            .statusCode(200)
            .body("status", equalTo("PENDING"))
            .body("shiftType", equalTo("NIGHT_SHIFT"))
            .body("shiftLengthInHours", equalTo(8))
            .extract()
            .path("id");

    String rejectShiftRequestBody =
        """
                             {
                             "shiftDate": "2025-10-20T21:30:00",
                             "shiftLengthInHours": 8,
                             "shiftType": "NIGHT_SHIFT",
                             "shiftRequestStatus": "REJECTED",
                             "rejectionReason": "Have important plans on this day."
                           }
                       """;

    RestAssured.given()
        .contentType(ContentType.JSON)
        .body(rejectShiftRequestBody)
        .when()
        .put(LANDING_PAGE + REJECT_SHIFT_REQUEST, shiftRequestId)
        .then()
        .statusCode(200)
        .body("status", equalTo("REJECTED"))
        .body("shiftType", equalTo("NIGHT_SHIFT"))
        .body("shiftLengthInHours", equalTo(8));
  }

  @Test
  void retrieveEmployeeShiftRequestsSuccessfully() {
    String shiftRequestBodyOne =
        """
                              {
                              "shiftDate": "2025-06-20T21:30:00",
                              "shiftLengthInHours": 8,
                              "shiftType": "NIGHT_SHIFT",
                              "shiftRequestStatus": "PENDING",
                              "rejectionReason": null,
                             "employee": {
                                "id": %d
                      }
                }
                """
            .formatted(employeeId);

    RestAssured.given()
        .contentType(ContentType.JSON)
        .body(shiftRequestBodyOne)
        .when()
        .post(LANDING_PAGE + CREATE_SHIFT_REQUEST)
        .then()
        .statusCode(200)
        .body("status", equalTo("PENDING"))
        .body("shiftType", equalTo("NIGHT_SHIFT"))
        .body("shiftLengthInHours", equalTo(8));

    String shiftRequestBodyTwo =
        """
                              {
                              "shiftDate": "2025-06-20T06:30:00",
                              "shiftLengthInHours": 8,
                              "shiftType": "MORNING_SHIFT",
                              "shiftRequestStatus": "PENDING",
                              "rejectionReason": null,
                             "employee": {
                                "id": %d
                      }
                }
                """
            .formatted(employeeId);

    RestAssured.given()
        .contentType(ContentType.JSON)
        .body(shiftRequestBodyTwo)
        .when()
        .post(LANDING_PAGE + CREATE_SHIFT_REQUEST)
        .then()
        .statusCode(200)
        .body("status", equalTo("PENDING"))
        .body("shiftType", equalTo("MORNING_SHIFT"))
        .body("shiftLengthInHours", equalTo(8));

    RestAssured.given()
        .pathParam("employeeId", employeeId)
        .contentType(ContentType.JSON)
        .when()
        .get(LANDING_PAGE + GET_SHIFT_REQUESTS)
        .then()
        .statusCode(200)
        .body("findAll { it.shiftType == 'NIGHT_SHIFT' }.size()", greaterThanOrEqualTo(1))
        .body("findAll { it.shiftType == 'MORNING_SHIFT' }.size()", greaterThanOrEqualTo(1));
  }

  @Test
  void getShiftRequestsByGivenDateRange() {
    String shiftRequestBodyOne =
        """
            {
              "shiftType": "MORNING_SHIFT",
              "shiftLengthInHours": 8,
              "shiftDate": "2025-06-15T08:00:00",
              "shiftRequestStatus": "PENDING",
              "rejectionReason": null,
              "employee": {
                "id": %d
              }
            }
            """
            .formatted(employeeId);

    RestAssured.given()
        .contentType(ContentType.JSON)
        .body(shiftRequestBodyOne)
        .when()
        .post(LANDING_PAGE + CREATE_SHIFT_REQUEST)
        .then()
        .statusCode(200)
        .body("status", equalTo("PENDING"));

    String shiftRequestBodyTwo =
        """
            {
              "shiftType": "NIGHT_SHIFT",
              "shiftLengthInHours": 8,
              "shiftDate": "2025-07-20T20:00:00",
              "shiftRequestStatus": "PENDING",
              "rejectionReason": null,
              "employee": {
                "id": %d
              }
            }
            """
            .formatted(employeeId);

    RestAssured.given()
        .contentType(ContentType.JSON)
        .body(shiftRequestBodyTwo)
        .when()
        .post(LANDING_PAGE + CREATE_SHIFT_REQUEST)
        .then()
        .statusCode(200)
        .body("status", equalTo("PENDING"));

    String startDate = "2025-05-01T00:00:00";
    String endDate = "2025-07-30T23:59:59";

    RestAssured.given()
        .pathParam("employeeId", employeeId)
        .queryParam("startDate", startDate)
        .queryParam("endDate", endDate)
        .contentType(ContentType.JSON)
        .when()
        .get(LANDING_PAGE + SHIFT_REQUESTS_DATE_RANGE)
        .then()
        .statusCode(200)
        .body("findAll { it.shiftType == 'MORNING_SHIFT' }.size()", greaterThanOrEqualTo(1))
        .body("findAll { it.shiftType == 'NIGHT_SHIFT' }.size()", greaterThanOrEqualTo(1));
  }
}
