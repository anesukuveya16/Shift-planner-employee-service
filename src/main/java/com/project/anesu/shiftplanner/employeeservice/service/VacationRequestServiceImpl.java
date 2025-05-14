package com.project.anesu.shiftplanner.employeeservice.service;

import com.project.anesu.shiftplanner.employeeservice.entity.vacation.VacationRequest;
import com.project.anesu.shiftplanner.employeeservice.entity.vacation.VacationRequestStatus;
import com.project.anesu.shiftplanner.employeeservice.model.VacationRequestService;
import com.project.anesu.shiftplanner.employeeservice.model.repository.VacationRequestRepository;
import com.project.anesu.shiftplanner.employeeservice.service.exception.VacationRequestNotFoundException;
import com.project.anesu.shiftplanner.employeeservice.service.util.VacationRequestValidator;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class VacationRequestServiceImpl implements VacationRequestService {

  private final VacationRequestRepository vacationRequestRepository;
  private final VacationRequestValidator vacationRequestValidator;

  @Override
  public VacationRequest createVacationRequest(VacationRequest vacationRequest) {

    validateVacationRequest(vacationRequest);
    vacationRequest.setStatus(VacationRequestStatus.PENDING);
    return vacationRequestRepository.save(vacationRequest);
  }

  @Override
  public VacationRequest withdrawVacationRequest(Long vacationRequestId, Long employeeId)
      throws VacationRequestNotFoundException {

    VacationRequest vacationRequest = getVacationRequestById(vacationRequestId);

    vacationRequestValidator.validateWithdrawalRequest(employeeId, vacationRequest);

    vacationRequest.setStatus(VacationRequestStatus.WITHDRAWN);

    return vacationRequestRepository.save(vacationRequest);
  }

  @Override
  public List<VacationRequest> getVacationRequestsByEmployeeId(Long employeeId) {

    return vacationRequestRepository.findByEmployeeId(employeeId);
  }

  @Override
  public List<VacationRequest> getVacationRequestsByDateRange(
      Long employeeId, LocalDateTime startDate, LocalDateTime endDate) {

    return vacationRequestRepository.findByEmployeeIdAndDateRange(employeeId, startDate, endDate);
  }

  @Override
  public List<VacationRequest> getTeamCalendar(
      Long officeLocationId, LocalDateTime startDate, LocalDateTime endDate) {

    return vacationRequestRepository.findByOfficeLocationAndDateRangeAndStatus(
        officeLocationId,
        startDate,
        endDate,
        List.of(VacationRequestStatus.APPROVED, VacationRequestStatus.PENDING));
  }

  private VacationRequest getVacationRequestById(Long vacationRequestId)
      throws VacationRequestNotFoundException {

    return vacationRequestRepository
        .findById(vacationRequestId)
        .orElseThrow(
            () ->
                new VacationRequestNotFoundException(
                    "Vacation request not found with ID: " + vacationRequestId));
  }

  private void validateVacationRequest(VacationRequest vacationRequest)
      throws VacationRequestNotFoundException {

    vacationRequestValidator.validateVacationRequest(vacationRequest, vacationRequestRepository);
  }
}
