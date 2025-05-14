package com.project.anesu.shiftplanner.employeeservice.service.util;

import com.project.anesu.shiftplanner.employeeservice.entity.vacation.VacationRequest;
import com.project.anesu.shiftplanner.employeeservice.entity.vacation.VacationRequestStatus;
import com.project.anesu.shiftplanner.employeeservice.model.repository.VacationRequestRepository;
import com.project.anesu.shiftplanner.employeeservice.service.exception.InvalidVacationRequestException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class VacationRequestValidator {

  private static final int MAX_VACATION_DAYS_EACH_YEAR = 30;

  private static final String OVERLAPPING_VACATION_REQUEST_ERROR =
      "Request could not be fulfilled because there is already an approved vacation request for this period for employee: ";

  private static final String INVALID_VACATION_STATUS_ERROR =
      "Only pending requests can be withdrawn.";

  private static final String INVALID_VACATION_OWNER_ERROR =
      "You can only withdraw your own requests.";

  public void validateVacationRequest(
      VacationRequest vacationRequest, VacationRequestRepository repository) {

    validateOverlappingRequests(vacationRequest, repository);
    validateRemainingVacationDays(vacationRequest, repository);
  }

  public void validateWithdrawalRequest(Long employeeId, VacationRequest vacationRequest) {

    if (!vacationRequest.getStatus().equals(VacationRequestStatus.PENDING)) {
      throw new InvalidVacationRequestException(INVALID_VACATION_STATUS_ERROR);
    }

    if (!vacationRequest.getEmployee().getId().equals(employeeId)) {
      throw new SecurityException(INVALID_VACATION_OWNER_ERROR);
    }
  }

  private void validateOverlappingRequests(
      VacationRequest vacationRequest, VacationRequestRepository repository) {

    if (isOverlappingWithExistingRequests(vacationRequest, repository)) {
      throw new InvalidVacationRequestException(
          OVERLAPPING_VACATION_REQUEST_ERROR + vacationRequest.getEmployee().getId());
    }
  }

  private boolean isOverlappingWithExistingRequests(
      VacationRequest vacationRequest, VacationRequestRepository repository) {

    Long employeeId = vacationRequest.getEmployee().getId();
    return repository
        .findByEmployeeIdAndDateRange(
            employeeId, vacationRequest.getStartDate(), vacationRequest.getEndDate())
        .stream()
        .anyMatch(
            existingRequest ->
                existingRequest.getStatus().equals(VacationRequestStatus.APPROVED)
                    || existingRequest.getStatus().equals(VacationRequestStatus.PENDING));
  }

  private void validateRemainingVacationDays(
      VacationRequest vacationRequest, VacationRequestRepository repository) {

    List<VacationRequest> usedVacationDaysInCurrentYear =
        getUsedVacationDaysForCurrentYear(vacationRequest, repository);

    long existingUsedDays = calculateTotalUsedVacationDays(usedVacationDaysInCurrentYear);

    int newRequestedDays = calculateRequestedVacationDays(vacationRequest);

    long totalVacationDays = existingUsedDays + newRequestedDays;

    if (totalVacationDays > MAX_VACATION_DAYS_EACH_YEAR) {
      throw new InvalidVacationRequestException(
          String.format(
              "Vacation request exceeds yearly limit. Employee ID: %d already has %d days. New request adds %d days, exceeding the maximum of %d days.",
              vacationRequest.getEmployee().getId(),
              existingUsedDays,
              newRequestedDays,
              MAX_VACATION_DAYS_EACH_YEAR));
    }
  }

  private List<VacationRequest> getUsedVacationDaysForCurrentYear(
      VacationRequest vacationRequest, VacationRequestRepository repository) {

    int currentYear = LocalDate.now().getYear();
    LocalDate startOfYear = LocalDate.of(currentYear, 1, 1);
    LocalDate endOfYear = LocalDate.of(currentYear, 12, 31);

    return repository.findByEmployeeIdAndYearOverlap(
        vacationRequest.getEmployee().getId(), startOfYear, endOfYear);
  }

  private long calculateTotalUsedVacationDays(List<VacationRequest> vacationRequests) {

    return vacationRequests.stream()
        .mapToLong(vacation -> calculateDaysInRange(vacation.getStartDate(), vacation.getEndDate()))
        .sum();
  }

  private long calculateDaysInRange(LocalDateTime startDate, LocalDateTime endDate) {

    LocalDate adjustedStartDate =
        startDate.isBefore(LocalDateTime.now().withDayOfYear(1))
            ? LocalDate.now().withDayOfYear(1)
            : LocalDate.from(startDate);
    LocalDate adjustedEndDate =
        endDate.isAfter(LocalDateTime.now().withMonth(12).withDayOfMonth(31))
            ? LocalDate.now().withMonth(12).withDayOfMonth(31)
            : LocalDate.from(endDate);
    return ChronoUnit.DAYS.between(adjustedStartDate, adjustedEndDate) + 1; // Inclusive
  }

  private int calculateRequestedVacationDays(VacationRequest vacationRequest) {

    return (int)
            ChronoUnit.DAYS.between(vacationRequest.getStartDate(), vacationRequest.getEndDate())
        + 1;
  }
}
