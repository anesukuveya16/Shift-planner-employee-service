package com.project.anesu.shiftplanner.employeeservice.controller;

import static org.springframework.http.ResponseEntity.ok;

import com.project.anesu.shiftplanner.employeeservice.entity.schedule.Schedule;
import com.project.anesu.shiftplanner.employeeservice.entity.shift.ShiftRequest;
import com.project.anesu.shiftplanner.employeeservice.entity.vacation.VacationRequest;
import com.project.anesu.shiftplanner.employeeservice.model.ScheduleService;
import com.project.anesu.shiftplanner.employeeservice.model.ShiftRequestService;
import com.project.anesu.shiftplanner.employeeservice.model.VacationRequestService;
import com.project.anesu.shiftplanner.employeeservice.service.exception.InvalidScheduleException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(EmployeeServiceRestEndpoints.LANDING_PAGE)
@RequiredArgsConstructor
public class EmployeeController {

  private final ScheduleService scheduleService;
  private final ShiftRequestService shiftRequestService;
  private final VacationRequestService vacationRequestService;

  @PostMapping(EmployeeServiceRestEndpoints.CREATE_SCHEDULE)
  public Schedule createSchedule(@PathVariable Long employeeId, @RequestBody Schedule schedule) {
    if (!employeeId.equals(schedule.getEmployeeId())) {
      throw new InvalidScheduleException(
          "Given employee id is not the same as the employee id in the schedule to be created.");
    }
    return scheduleService.createSchedule(schedule);
  }

  @PutMapping(EmployeeServiceRestEndpoints.UPDATE_SCHEDULE)
  public ResponseEntity<String> updateSchedule(
      @PathVariable Long scheduleId, @RequestBody Schedule updatedSchedule) {
    Schedule updated = scheduleService.updateSchedule(scheduleId, updatedSchedule);

    if (updated != null) {
      return ResponseEntity.status(HttpStatus.OK).build();
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Schedule not found");
    }
  }

  @DeleteMapping(EmployeeServiceRestEndpoints.DELETE_SCHEDULE)
  public ResponseEntity<String> deleteSchedule(@PathVariable Long scheduleId) {
    scheduleService.deleteSchedule(scheduleId);
    return ok("Schedule deleted successfully.");
  }

  @GetMapping(EmployeeServiceRestEndpoints.GET_SCHEDULES_BY_DATE_RANGE)
  public List<Schedule> getEmployeeSchedules(
      @PathVariable Long employeeId,
      @RequestParam("startDate") LocalDateTime startDate,
      @RequestParam("endDate") LocalDateTime endDate) {
    return scheduleService
        .getSchedulesByEmployeeAndDateRange(employeeId, startDate, endDate)
        .orElse(Collections.emptyList());
  }

  @PostMapping(EmployeeServiceRestEndpoints.CREATE_SHIFT_REQUEST)
  public ShiftRequest createShiftRequest(@RequestBody ShiftRequest shiftRequest) {
    return shiftRequestService.createShiftRequest(shiftRequest);
  }

  @PutMapping(EmployeeServiceRestEndpoints.APPROVE_SHIFT_REQUEST)
  public ShiftRequest approveShiftRequest(
      @PathVariable Long employeeId, @PathVariable Long shiftRequestId) {
    return shiftRequestService.approveShiftRequest(employeeId, shiftRequestId);
  }

  @PutMapping(EmployeeServiceRestEndpoints.REJECT_SHIFT_REQUEST)
  public ShiftRequest rejectShiftRequest(
      @PathVariable Long shiftRequestId, @RequestParam String rejectionReason) {
    return shiftRequestService.rejectShiftRequest(shiftRequestId, rejectionReason);
  }

  @GetMapping(EmployeeServiceRestEndpoints.GET_SHIFT_REQUESTS)
  public List<ShiftRequest> getEmployeeShiftRequests(@PathVariable Long employeeId) {
    return shiftRequestService.getShiftRequestsByEmployee(employeeId);
  }

  @GetMapping(EmployeeServiceRestEndpoints.SHIFT_REQUESTS_DATE_RANGE)
  public List<ShiftRequest> getShiftRequestsByDateRange(
      @PathVariable Long employeeId,
      @RequestParam("startDate") LocalDateTime startDate,
      @RequestParam("endDate") LocalDateTime endDate) {
    return shiftRequestService.getShiftRequestsByDateRange(employeeId, startDate, endDate);
  }

  @PostMapping(EmployeeServiceRestEndpoints.CREATE_VACATION_REQUEST)
  public VacationRequest createVacationRequest(@RequestBody VacationRequest vacationRequest) {
    return vacationRequestService.createVacationRequest(vacationRequest);
  }

  @PutMapping(EmployeeServiceRestEndpoints.WITHDRAW_VACATION_REQUEST)
  public VacationRequest withdrawVacationRequest(
      @PathVariable Long employeeId, @PathVariable Long vacationRequestId) {
    return vacationRequestService.withdrawVacationRequest(vacationRequestId, employeeId);
  }

  @GetMapping(EmployeeServiceRestEndpoints.GET_VACATION_REQUESTS)
  public List<VacationRequest> getEmployeeVacationRequests(@PathVariable Long employeeId) {
    return vacationRequestService.getVacationRequestsByEmployeeId(employeeId);
  }

  @GetMapping(EmployeeServiceRestEndpoints.VACATION_REQUESTS_DATE_RANGE)
  public List<VacationRequest> getVacationRequestsByDateRange(
      @PathVariable Long employeeId,
      @RequestParam("startDate") LocalDateTime startDate,
      @RequestParam("endDate") LocalDateTime endDate) {
    return vacationRequestService.getVacationRequestsByDateRange(employeeId, startDate, endDate);
  }
}
