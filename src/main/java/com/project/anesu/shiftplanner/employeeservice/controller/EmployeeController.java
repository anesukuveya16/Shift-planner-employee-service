package com.project.anesu.shiftplanner.employeeservice.controller;

import static com.project.anesu.shiftplanner.employeeservice.controller.EmployeeServiceRestEndpoints.*;

import com.project.anesu.shiftplanner.employeeservice.entity.employee.Employee;
import com.project.anesu.shiftplanner.employeeservice.entity.schedule.Schedule;
import com.project.anesu.shiftplanner.employeeservice.entity.shift.ShiftRequest;
import com.project.anesu.shiftplanner.employeeservice.entity.vacation.VacationRequest;
import com.project.anesu.shiftplanner.employeeservice.model.ScheduleService;
import com.project.anesu.shiftplanner.employeeservice.model.ShiftRequestService;
import com.project.anesu.shiftplanner.employeeservice.model.VacationRequestService;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(LANDING_PAGE)
public class EmployeeController {

  private final ScheduleService scheduleService;
  private final ShiftRequestService shiftRequestService;
  private final VacationRequestService vacationRequestService;

  @PostMapping(CREATE_SCHEDULE)
  public Schedule createSchedule(@PathVariable Long employeeId, @RequestBody Schedule schedule) {

    Employee employee = new Employee();
    employee.setId(employeeId);
    schedule.setEmployee(employee);

    return scheduleService.createSchedule(schedule);
  }

  @PutMapping(UPDATE_SCHEDULE)
  public ResponseEntity<Void> updateSchedule(
      @PathVariable Long scheduleId, @RequestBody Schedule updatedSchedule) {
    Schedule updated = scheduleService.updateSchedule(scheduleId, updatedSchedule);

    return updated != null
        ? ResponseEntity.status(HttpStatus.OK).build()
        : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  }

  @DeleteMapping(DELETE_SCHEDULE)
  public ResponseEntity<Void> deleteSchedule(@PathVariable Long scheduleId) {
    scheduleService.deleteSchedule(scheduleId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping(GET_SCHEDULES_BY_DATE_RANGE)
  public List<Schedule> getSchedulesByEmployeeAndDateRange(
      @PathVariable Long employeeId,
      @RequestParam("startDate") LocalDateTime startDate,
      @RequestParam("endDate") LocalDateTime endDate) {
    return scheduleService
        .getSchedulesByEmployeeAndDateRange(employeeId, startDate, endDate)
        .orElse(Collections.emptyList());
  }

  @PostMapping(CREATE_SHIFT_REQUEST)
  public ShiftRequest createShiftRequest(@RequestBody ShiftRequest shiftRequest) {
    return shiftRequestService.createShiftRequest(shiftRequest);
  }

  @PutMapping(APPROVE_SHIFT_REQUEST)
  public ShiftRequest approveShiftRequest(
      @PathVariable Long employeeId, @PathVariable Long shiftRequestId) {
    return shiftRequestService.approveShiftRequest(employeeId, shiftRequestId);
  }

  @PutMapping(REJECT_SHIFT_REQUEST)
  public ShiftRequest rejectShiftRequest(
      @PathVariable Long shiftRequestId, @RequestBody String rejectionReason) {
    return shiftRequestService.rejectShiftRequest(shiftRequestId, rejectionReason);
  }

  @GetMapping(GET_SHIFT_REQUESTS)
  public List<ShiftRequest> getEmployeeShiftRequests(@PathVariable Long employeeId) {
    return shiftRequestService.getShiftRequestsByEmployee(employeeId);
  }

  @GetMapping(SHIFT_REQUESTS_DATE_RANGE)
  public List<ShiftRequest> getShiftRequestsByDateRange(
      @PathVariable Long employeeId,
      @RequestParam("startDate") LocalDateTime startDate,
      @RequestParam("endDate") LocalDateTime endDate) {
    return shiftRequestService.getShiftRequestsByDateRange(employeeId, startDate, endDate);
  }

  @PostMapping(CREATE_VACATION_REQUEST)
  public VacationRequest createVacationRequest(@RequestBody VacationRequest vacationRequest) {
    return vacationRequestService.createVacationRequest(vacationRequest);
  }

  @PutMapping(WITHDRAW_VACATION_REQUEST)
  public VacationRequest withdrawVacationRequest(
      @PathVariable Long employeeId, @PathVariable Long vacationRequestId) {
    return vacationRequestService.withdrawVacationRequest(vacationRequestId, employeeId);
  }

  @GetMapping(GET_VACATION_REQUESTS)
  public List<VacationRequest> getEmployeeVacationRequests(@PathVariable Long employeeId) {
    return vacationRequestService.getVacationRequestsByEmployeeId(employeeId);
  }

  @GetMapping(VACATION_REQUESTS_DATE_RANGE)
  public List<VacationRequest> getVacationRequestsByDateRange(
      @PathVariable Long employeeId,
      @RequestParam("startDate") LocalDateTime startDate,
      @RequestParam("endDate") LocalDateTime endDate) {
    return vacationRequestService.getVacationRequestsByDateRange(employeeId, startDate, endDate);
  }
}
