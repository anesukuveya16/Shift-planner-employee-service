package com.project.anesu.shiftplanner.employeeservice.service.util;

import com.project.anesu.shiftplanner.employeeservice.entity.shift.ShiftRequest;
import com.project.anesu.shiftplanner.employeeservice.model.repository.ShiftRequestRepository;
import com.project.anesu.shiftplanner.employeeservice.service.exception.ShiftValidationException;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class ShiftRequestValidator {

  private static final int MAX_LEGAL_WORKING_HOURS = 10;

  public void validateShiftRequest(
      ShiftRequest shiftRequest, ShiftRequestRepository shiftRequestRepository) {

    Optional<ShiftRequest> shiftRequestOptional =
        shiftRequestRepository.findByEmployeeIdAndShiftDate(
            shiftRequest.getEmployeeId(), shiftRequest.getShiftDate());

    shiftRequestOptional.ifPresent(
        existingShift -> {
          boolean exceedsMaximumWorkingHours =
              existingShift.getShiftLengthInHours() + shiftRequest.getShiftLengthInHours()
                  >= MAX_LEGAL_WORKING_HOURS;

          if (exceedsMaximumWorkingHours) {
            throw new ShiftValidationException(
                "New shift request violates working hours. Employee ID: "
                    + shiftRequest.getEmployeeId()
                    + " already has "
                    + shiftRequestOptional.get().getShiftLengthInHours()
                    + " hours for this shift scheduled/recorded. Maximum working hours should not exceed : "
                    + MAX_LEGAL_WORKING_HOURS
                    + " hours.");
          }
        });
  }
}
