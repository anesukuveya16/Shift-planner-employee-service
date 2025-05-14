package com.project.anesu.shiftplanner.employeeservice.model;

import com.project.anesu.shiftplanner.employeeservice.entity.shift.ShiftRequest;
import com.project.anesu.shiftplanner.employeeservice.entity.shift.ShiftRequestStatus;
import com.project.anesu.shiftplanner.employeeservice.service.exception.ShiftRequestNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Used for managing shift requests for employees.
 *
 * <p>This service provides business logic and operations related to shift requests, such as
 * creating, updating, approving, or rejecting shift requests. It acts as the intermediary between
 * the controller layer and the data access layer.
 *
 * <h2>Key Responsibilities:</h2>
 *
 * <ul>
 *   <li>Create new shift requests for employees.
 *   <li>Retrieve shift requests by employee, date, or status.
 *   <li>Update the status of shift requests (e.g., approve, reject).
 *   <li>Ensure business rules for shift scheduling are adhered to.
 * </ul>
 *
 * @author Anesu
 * @version 1.0
 * @since 2024-12-13
 */
public interface ShiftRequestService {

  /**
   * Creates a new shift request.
   *
   * @param shiftRequest the shift request to create
   * @return the created shift request
   */
  ShiftRequest createShiftRequest(ShiftRequest shiftRequest);

  /**
   * Approves a shift request by ID.
   *
   * @param shiftRequestId the ID of the shift request to approve
   * @return the updated shift request
   */
  ShiftRequest approveShiftRequest(Long employeeId, Long shiftRequestId)
      throws ShiftRequestNotFoundException;

  /**
   * Rejects a shift request by ID.
   *
   * @param shiftRequestId the ID of the shift request to reject
   * @param rejectionReason the reason for rejecting the shift request
   * @return the updated shift request
   */
  ShiftRequest rejectShiftRequest(Long shiftRequestId, String rejectionReason)
      throws ShiftRequestNotFoundException;

  /**
   * Retrieves a shift request by its ID.
   *
   * @param shiftRequestId the ID of the shift request
   * @return the shift request
   */
  ShiftRequest getShiftRequestByIdAndStatus(Long shiftRequestId, ShiftRequestStatus status)
      throws ShiftRequestNotFoundException;

  /**
   * Retrieves shift requests by employee ID.
   *
   * @param employeeId the ID of the employee
   * @return a list of shift requests for the specified employee
   */
  List<ShiftRequest> getShiftRequestsByEmployee(Long employeeId);

  /**
   * Retrieves shift requests within a specific date range.
   *
   * @param startDate the start date
   * @param endDate the end date
   * @return a list of shift requests in the specified date range
   */
  List<ShiftRequest> getShiftRequestsByDateRange(
      Long employeeId, LocalDateTime startDate, LocalDateTime endDate);
}
