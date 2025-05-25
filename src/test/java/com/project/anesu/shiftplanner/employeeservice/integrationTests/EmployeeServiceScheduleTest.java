package com.project.anesu.shiftplanner.employeeservice.integrationTests;

import static com.project.anesu.shiftplanner.employeeservice.controller.EmployeeServiceRestEndpoints.*;
import static org.hamcrest.Matchers.equalTo;

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
class EmployeeServiceScheduleTest {

  @LocalServerPort private int port;

  @Autowired EmployeeRepository employeeRepository;

  private Long employeeId;

  @BeforeEach
  void setUp() {
    RestAssured.port = port;

    Employee employee = new Employee();

    employee.setFirstName("Lisa");
    employee.setLastName("Simpson");
    employee.setPhone("0123456789");
    employee.setEmail("lisa@gmail.com");
    employee.setPersonUuid(UUID.randomUUID());
    employee.setAddress("Holler street 134, 22525 Hamburg");
    employee.setBirthDate(LocalDate.of(1989, 3, 16));

    Employee savedEmployee = employeeRepository.save(employee);
    employeeId = savedEmployee.getId();
  }

  @Test
  void shouldSuccessfullyCreateNewScheduleForEmployee() {

    String scheduleRequestBody =
        """
            {
                "startDate": "2025-01-15T08:00:00",
                "endDate": "2025-01-15T16:00:00",
                "rejectionReason": null,
                "shifts": [],
                "vacations": []
            }
        """;

    RestAssured.given()
        .pathParam("employeeId", employeeId)
        .contentType(ContentType.JSON)
        .body(scheduleRequestBody)
        .when()
        .post(LANDING_PAGE + CREATE_SCHEDULE)
        .then()
        .log()
        .ifValidationFails()
        .statusCode(200)
        .body("shifts.size()", equalTo(0))
        .body("vacations.size()", equalTo(0));
  }

  @Test
  void shouldSuccessfullyUpdateSchedule() {

    String scheduleRequestBody =
        """
                    {
                      "startDate": "2025-01-15T08:00:00",
                      "endDate": "2025-01-15T16:00:00",
                      "rejectionReason": null,
                      "shifts": [],
                      "vacations": []
                    }
                """;

    Integer scheduleId =
        RestAssured.given()
            .pathParam("employeeId", employeeId)
            .contentType(ContentType.JSON)
            .body(scheduleRequestBody)
            .when()
            .post(LANDING_PAGE + CREATE_SCHEDULE)
            .then()
            .statusCode(200)
            .body("shifts.size()", equalTo(0))
            .body("vacations.size()", equalTo(0))
            .extract()
            .path("id");

    String updateScheduleRequestBody =
        """
                   {
                     "startDate": "2025-01-15T10:15:00",
                     "endDate": "2025-12-31T08:00:00",
                     "rejectionReason": null,
                     "shifts": [],
                     "vacations": []
                   }
               """;

    RestAssured.given()
        .contentType(ContentType.JSON)
        .body(updateScheduleRequestBody)
        .when()
        .put(LANDING_PAGE + UPDATE_SCHEDULE, scheduleId)
        .then()
        .log()
        .ifValidationFails()
        .statusCode(200);
  }

  @Test
  void shouldSuccessfullyDeleteSchedule() {
    String currentScheduleRequestBody =
        """
                    {
                      "startDate": "2025-01-15T08:00:00",
                      "endDate": "2025-01-15T16:00:00",
                      "rejectionReason": null,
                      "shifts": [],
                      "vacations": []
                    }
                """;

    Integer scheduleId =
        RestAssured.given()
            .pathParam("employeeId", employeeId)
            .contentType(ContentType.JSON)
            .body(currentScheduleRequestBody)
            .when()
            .post(LANDING_PAGE + CREATE_SCHEDULE)
            .then()
            .statusCode(200)
            .body("shifts.size()", equalTo(0))
            .body("vacations.size()", equalTo(0))
            .extract()
            .path("id");

    RestAssured.given()
        .contentType(ContentType.JSON)
        .when()
        .delete(LANDING_PAGE + DELETE_SCHEDULE, scheduleId);
  }

  @Test
  void shouldRetrieveScheduleByGivenDateRange () {
    String scheduleRequestBody =
            """
                {
                    "startDate": "2025-01-15T08:00:00",
                    "endDate": "2025-05-31T16:00:00",
                    "rejectionReason": null,
                    "shifts": [],
                    "vacations": []
                }
            """;

    RestAssured.given()
            .pathParam("employeeId", employeeId)
            .contentType(ContentType.JSON)
            .body(scheduleRequestBody)
            .when()
            .post(LANDING_PAGE + CREATE_SCHEDULE)
            .then()
            .log()
            .ifValidationFails()
            .statusCode(200)
            .body("shifts.size()", equalTo(0))
            .body("vacations.size()", equalTo(0));


    String startDate = "2025-01-01T00:00:00";
    String endDate = "2025-08-30T23:59:59";

    RestAssured.given()
            .pathParam("employeeId", employeeId)
            .queryParam("startDate", startDate)
            .queryParam("endDate", endDate)
            .contentType(ContentType.JSON)
            .when()
            .get(LANDING_PAGE + GET_SCHEDULES_BY_DATE_RANGE)
            .then()
            .statusCode(200);

  }
}
