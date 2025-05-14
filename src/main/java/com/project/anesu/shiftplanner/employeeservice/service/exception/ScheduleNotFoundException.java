package com.project.anesu.shiftplanner.employeeservice.service.exception;

public class ScheduleNotFoundException extends RuntimeException {
  public ScheduleNotFoundException(String message) {
    super(message);
  }
}
