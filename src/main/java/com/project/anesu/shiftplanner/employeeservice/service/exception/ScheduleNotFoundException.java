package com.project.anesu.shiftplanner.employeeservice.service.exception;

public class ScheduleNotFoundException extends RuntimeException {
  private static final String SCHEDULE_NOT_FOUND_EXCEPTION_MESSAGE =
      "Schedule not found with id: %s";

  public ScheduleNotFoundException(Long scheduleId) {
    super(SCHEDULE_NOT_FOUND_EXCEPTION_MESSAGE.formatted(scheduleId));
  }
}
