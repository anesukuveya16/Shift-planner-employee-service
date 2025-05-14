package com.project.anesu.shiftplanner.employeeservice.model;

import com.project.anesu.shiftplanner.employeeservice.entity.schedule.Schedule;
import com.project.anesu.shiftplanner.employeeservice.entity.shift.ShiftRequest;
import com.project.anesu.shiftplanner.employeeservice.service.exception.ScheduleNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ScheduleService {

  /** Service interface for managing employee schedules. */

  /**
   * Creates a new {@link Schedule} for an employee.
   *
   * @param schedule the {@link Schedule} to be created
   * @return the created {@link Schedule}
   */
  Schedule createSchedule(Schedule schedule);

  /**
   * Updates an existing {@link Schedule} that was already approved.
   *
   * @param scheduleId the ID of the {@link Schedule} to update
   * @param updatedSchedule the updated {@link Schedule} data
   * @return the updated {@link Schedule}
   */
  Schedule updateSchedule(Long scheduleId, Schedule updatedSchedule);

  /**
   * Updates the {@link Schedule} to add a new shift entry from the just approved {@link
   * ShiftRequest}.
   *
   * @param approvedShiftRequest the approved {@link ShiftRequest}
   * @return the updated {@link Schedule}
   */
  Schedule addShiftToSchedule(Long employeeId, ShiftRequest approvedShiftRequest);

  /**
   * Retrieves a {@link Schedule} by its ID.
   *
   * @param scheduleId the ID of the {@link Schedule} to retrieve
   * @return the found {@link Schedule}, or {@code null} if not found
   */
  Optional<Schedule> getScheduleById(Long scheduleId);

  /**
   * Retrieves schedules for a specific employee within a date range.
   *
   * @param employeeId the ID of the employee
   * @param startDate the start of the {@link LocalDateTime} range
   * @param endDate the end of the {@link LocalDateTime} range
   * @return a list of matching {@link Schedule}s
   */
  Optional<List<Schedule>> getSchedulesByEmployeeAndDateRange(
      Long employeeId, LocalDateTime startDate, LocalDateTime endDate);

  /**
   * Deletes a {@link Schedule} by its ID.
   *
   * @param scheduleId the ID of the {@link Schedule} to delete
   */
  void deleteSchedule(Long scheduleId) throws ScheduleNotFoundException;
}
