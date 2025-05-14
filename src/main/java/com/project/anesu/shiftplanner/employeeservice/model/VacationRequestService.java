package com.project.anesu.shiftplanner.employeeservice.model;

import com.project.anesu.shiftplanner.employeeservice.entity.vacation.VacationRequest;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for managing vacation requests. Provides methods for creating, withdrawing, and
 * retrieving vacation requests, as well as fetching vacation schedules for teams.
 */
public interface VacationRequestService {

  /**
   * Creates a new vacation request.
   *
   * @param vacationRequest The vacation request to be created.
   * @return The created {@link VacationRequest}.
   */
  VacationRequest createVacationRequest(VacationRequest vacationRequest);

  /**
   * Withdraws a vacation request for a specific employee.
   *
   * @param vacationRequestId The ID of the vacation request to withdraw.
   * @param employeeId The ID of the employee withdrawing the request.
   * @return The updated {@link VacationRequest} after withdrawal.
   */
  VacationRequest withdrawVacationRequest(Long vacationRequestId, Long employeeId);

  /**
   * Retrieves a list of vacation requests for a specific employee.
   *
   * @param employeeId The ID of the employee.
   * @return A list of {@link VacationRequest} objects for the given employee.
   */
  List<VacationRequest> getVacationRequestsByEmployeeId(Long employeeId);

  /**
   * Retrieves vacation requests for an employee within a specified date range.
   *
   * @param employeeId The ID of the employee.
   * @param startDate The start date of the requested range.
   * @param endDate The end date of the requested range.
   * @return A list of {@link VacationRequest} objects within the given date range.
   */
  List<VacationRequest> getVacationRequestsByDateRange(
      Long employeeId, LocalDateTime startDate, LocalDateTime endDate);

  /**
   * Retrieves the vacation schedule for a team within a specified date range.
   *
   * @param officeLocationId The ID of the office location.
   * @param startDate The start date of the requested schedule.
   * @param endDate The end date of the requested schedule.
   * @return A list of {@link VacationRequest} objects representing the team's calendar.
   */
  List<VacationRequest> getTeamCalendar(
      Long officeLocationId, LocalDateTime startDate, LocalDateTime endDate);
}
