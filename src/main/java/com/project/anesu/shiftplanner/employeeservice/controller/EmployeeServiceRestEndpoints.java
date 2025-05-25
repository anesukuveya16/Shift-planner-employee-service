package com.project.anesu.shiftplanner.employeeservice.controller;

public class EmployeeServiceRestEndpoints {
  public static final String LANDING_PAGE = "/api/employees";

  // Schedule
  public static final String CREATE_SCHEDULE = "/{employeeId}/schedule";
  public static final String UPDATE_SCHEDULE = "/schedule/{scheduleId}";
  public static final String DELETE_SCHEDULE = "/schedule/{scheduleId}";
  public static final String GET_SCHEDULES_BY_DATE_RANGE = "/{employeeId}/schedule";

  // Shift Requests
  public static final String CREATE_SHIFT_REQUEST = "/shift-request";
  public static final String APPROVE_SHIFT_REQUEST =
      "/{employeeId}/shift-request/{shiftRequestId}/approve";
  public static final String REJECT_SHIFT_REQUEST = "/{shiftRequestId}/reject";
  public static final String GET_SHIFT_REQUESTS = "/{employeeId}/shift-requests";
  public static final String SHIFT_REQUESTS_DATE_RANGE = "/{employeeId}/shift-requests/date-range";

  // Vacation Requests
  public static final String CREATE_VACATION_REQUEST = "/vacation-request";
  public static final String WITHDRAW_VACATION_REQUEST =
      "/{employeeId}/vacation-request/{vacationRequestId}/withdraw";
  public static final String GET_VACATION_REQUESTS = "/{employeeId}/vacation-requests";
  public static final String VACATION_REQUESTS_DATE_RANGE =
      "/{employeeId}/vacation-requests/date-range";

  private EmployeeServiceRestEndpoints() {}
}
