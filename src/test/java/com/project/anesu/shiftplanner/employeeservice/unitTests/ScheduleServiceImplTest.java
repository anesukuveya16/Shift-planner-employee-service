package com.project.anesu.shiftplanner.employeeservice.unitTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.project.anesu.shiftplanner.employeeservice.entity.employee.Employee;
import com.project.anesu.shiftplanner.employeeservice.entity.schedule.Schedule;
import com.project.anesu.shiftplanner.employeeservice.entity.shift.ShiftEntry;
import com.project.anesu.shiftplanner.employeeservice.entity.shift.ShiftRequest;
import com.project.anesu.shiftplanner.employeeservice.entity.shift.ShiftRequestStatus;
import com.project.anesu.shiftplanner.employeeservice.entity.shift.ShiftType;
import com.project.anesu.shiftplanner.employeeservice.model.repository.ScheduleRepository;
import com.project.anesu.shiftplanner.employeeservice.service.ScheduleServiceImpl;
import com.project.anesu.shiftplanner.employeeservice.service.exception.InvalidScheduleException;
import com.project.anesu.shiftplanner.employeeservice.service.exception.ScheduleNotFoundException;
import com.project.anesu.shiftplanner.employeeservice.service.util.ScheduleValidator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceImplTest {
  @Mock private ScheduleRepository scheduleRepositoryMock;
  @Mock private ScheduleValidator scheduleValidatorMock;

  private ScheduleServiceImpl cut;

  @BeforeEach
  void setUp() {
    cut = new ScheduleServiceImpl(scheduleRepositoryMock, scheduleValidatorMock);
  }

  @Test
  void createSchedule_ShouldSaveCreatedScheduleIfValidationIsPassed() {
    // Given
    Schedule schedule = new Schedule();

    doNothing().when(scheduleValidatorMock).validate(schedule);
    when(scheduleRepositoryMock.save(schedule)).thenReturn(schedule);
    // When

    Schedule createdSchedule = cut.createSchedule(schedule);
    // Then

    assertNotNull(createdSchedule);
    assertEquals(schedule, createdSchedule);
    verify(scheduleValidatorMock).validate(schedule);
    verify(scheduleRepositoryMock, times(1)).save(schedule);
  }

  @Test
  void createSchedule_ShouldNotSaveCreatedScheduleIfValidationHasFailed() {
    // Given
    Schedule schedule = new Schedule();

    doThrow(InvalidScheduleException.class).when(scheduleValidatorMock).validate(schedule);

    // When
    assertThrows(InvalidScheduleException.class, () -> cut.createSchedule(schedule));

    // Then
    verify(scheduleValidatorMock).validate(schedule);
    verifyNoMoreInteractions(scheduleRepositoryMock);
  }

  @Test
  void shouldUpdateGivenExistingScheduleWhenEmployeeMakesChanges() {
    // Given
    LocalDateTime startDate = LocalDate.now().plusDays(2).atTime(9, 0);
    Schedule oldSchedule = givenScheduleWithDurationAndStartDate(4, startDate);
    Schedule newSchedule = givenScheduleWithDurationAndStartDate(8, startDate);

    when(scheduleRepositoryMock.findById(oldSchedule.getId())).thenReturn(Optional.of(oldSchedule));
    doNothing().when(scheduleValidatorMock).validate(any(Schedule.class));
    when(scheduleRepositoryMock.save(any(Schedule.class))).thenReturn(newSchedule);

    // When
    Schedule updateSchedule = cut.updateSchedule(oldSchedule.getId(), newSchedule);

    // Then
    assertNotNull(updateSchedule);
    assertThat(updateSchedule.getTotalWorkingHours()).isEqualTo(newSchedule.getTotalWorkingHours());

    verify(scheduleRepositoryMock, times(1)).findById(oldSchedule.getId());
    verify(scheduleValidatorMock).validate(oldSchedule);
    verify(scheduleRepositoryMock, times(1)).save(oldSchedule);
  }

  @Test
  void shouldOnlyAddApprovedShiftRequestToSchedule() {
    // Given
    ShiftRequest approvedShiftRequest = givenShiftRequestWithStatus(ShiftRequestStatus.PENDING);

    // When
    InvalidScheduleException invalidScheduleException =
        assertThrows(
            InvalidScheduleException.class,
            () ->
                cut.addShiftToSchedule(
                    approvedShiftRequest.getEmployee().getId(), approvedShiftRequest));

    // Then
    assertThat(invalidScheduleException.getMessage())
        .isEqualTo(
            "Invalid schedule operation. ShiftRequest with ID: "
                + approvedShiftRequest.getId()
                + " is PENDING. Only approved shifts can be added to the schedule.");
  }

  @Test
  void shouldAddApprovedShiftRequestToSchedule() {
    // Given

    ShiftRequest approvedShiftRequest = givenShiftRequestWithStatus(ShiftRequestStatus.APPROVED);
    when(scheduleRepositoryMock.save(any(Schedule.class)))
        .thenReturn(createScheduleGivenApprovedShiftRequest(approvedShiftRequest, 1L));

    // When
    Schedule updatedSchedule =
        cut.addShiftToSchedule(approvedShiftRequest.getEmployee().getId(), approvedShiftRequest);

    // Then
    List<ShiftEntry> shifts = updatedSchedule.getShifts();
    assertThat(shifts).hasSize(1);
    ShiftEntry shiftEntry = shifts.getFirst();
    assertThat(shiftEntry.getShiftDate()).isEqualTo(approvedShiftRequest.getShiftDate());
    assertThat(shiftEntry.getWorkingHours())
        .isEqualTo(approvedShiftRequest.getShiftLengthInHours());
    assertThat(shiftEntry.getShiftType()).isEqualTo(approvedShiftRequest.getShiftType());
  }

  @Test
  void shouldNotUpdateScheduleAndThrowExceptionWhenScheduleIsNotFound() {
    // Given
    LocalDateTime startDate = LocalDate.now().plusDays(2).atTime(9, 0);
    Schedule oldSchedule = givenScheduleWithDurationAndStartDate(4, startDate);
    Schedule newSchedule = givenScheduleWithDurationAndStartDate(8, startDate);

    when(scheduleRepositoryMock.findById(oldSchedule.getId())).thenReturn(Optional.empty());

    // When
    assertThrows(
        ScheduleNotFoundException.class,
        () -> cut.updateSchedule(oldSchedule.getId(), newSchedule));

    // Then
    verify(scheduleRepositoryMock, times(1)).findById(oldSchedule.getId());
    verifyNoInteractions(scheduleValidatorMock);
    verifyNoMoreInteractions(scheduleRepositoryMock);
  }

  @Test
  void shouldNotUpdateScheduleAndThrowExceptionWhenUpdatedScheduleValidationFails() {
    // Given
    LocalDateTime startDate = LocalDateTime.now().plusDays(2);
    Schedule oldSchedule = givenScheduleWithDurationAndStartDate(4, startDate);
    Schedule newSchedule = givenScheduleWithDurationAndStartDate(8, startDate);

    when(scheduleRepositoryMock.findById(oldSchedule.getId())).thenReturn(Optional.of(oldSchedule));
    doThrow(InvalidScheduleException.class).when(scheduleValidatorMock).validate(oldSchedule);

    // When
    assertThrows(
        InvalidScheduleException.class, () -> cut.updateSchedule(oldSchedule.getId(), newSchedule));

    // Then
    verify(scheduleRepositoryMock, times(1)).findById(oldSchedule.getId());
    verify(scheduleValidatorMock).validate(oldSchedule);
    verifyNoMoreInteractions(scheduleRepositoryMock);
  }

  @Test
  void deleteSchedule_ShouldThrowExceptionWhenScheduleIsNotFound() {
    // Given
    long employeeId = 1L;
    doThrow(ScheduleNotFoundException.class).when(scheduleRepositoryMock).existsById(employeeId);

    // When
    assertThrows(ScheduleNotFoundException.class, () -> cut.deleteSchedule(employeeId));

    // Then
    verify(scheduleRepositoryMock, times(1)).existsById(employeeId);
    verifyNoMoreInteractions(scheduleRepositoryMock);
  }

  private Schedule givenScheduleWithDurationAndStartDate(
      long totalWorkingHours, LocalDateTime startDate) {
    Long employeeId = 1L;
    Schedule schedule = new Schedule();

    schedule.setStartDate(startDate);
    schedule.setEndDate(LocalDateTime.from(LocalDate.now().plusDays(2).atTime(15, 0)));
    schedule.setTotalWorkingHours(totalWorkingHours);
    return schedule;
  }

  private Schedule createScheduleGivenApprovedShiftRequest(
      ShiftRequest approvedShiftRequest, Long employeeId) {

    List<ShiftEntry> shiftEntries = new ArrayList<>();
    shiftEntries.add(ShiftEntry.fromApprovedShiftRequest(approvedShiftRequest));

    return Schedule.builder()
        .employee(Employee.builder().id(employeeId).build())
        .startDate(approvedShiftRequest.getShiftDate())
        .totalWorkingHours(approvedShiftRequest.getShiftLengthInHours())
        .shifts(shiftEntries)
        .build();
  }

  private ShiftRequest givenShiftRequestWithStatus(ShiftRequestStatus status) {
    Long shiftRequestId = 1L;
    Employee employee = new Employee();
    employee.setId(1L);

    ShiftRequest shiftRequest = new ShiftRequest();
    shiftRequest.setId(shiftRequestId);
    shiftRequest.setEmployee(employee);
    shiftRequest.setStatus(status);
    shiftRequest.setShiftLengthInHours(6L);
    shiftRequest.setShiftType(ShiftType.NIGHT_SHIFT);
    shiftRequest.setShiftDate(LocalDateTime.from(LocalDate.of(2024, 12, 29).atTime(8, 0)));

    return shiftRequest;
  }
}
