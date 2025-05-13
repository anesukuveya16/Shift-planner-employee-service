package com.project.anesu.shiftplanner.employeeservice.service.exception;

import com.project.anesu.shiftplanner.employeeservice.entity.shift.ShiftRequest;
import com.project.anesu.shiftplanner.employeeservice.entity.shift.ShiftRequestStatus;
import com.project.anesu.shiftplanner.employeeservice.model.ScheduleService;
import com.project.anesu.shiftplanner.employeeservice.model.ShiftRequestService;
import com.project.anesu.shiftplanner.employeeservice.model.repository.ShiftRequestRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShiftRequestServiceImpl implements ShiftRequestService {

    private final ShiftRequestRepository shiftRequestRepository;
    private final ShiftRequestValidator shiftRequestValidator;
    private final ScheduleService scheduleService;

    public ShiftRequestServiceImpl(
            ShiftRequestRepository shiftRequestRepository,
            ShiftRequestValidator shiftRequestValidator,
            ScheduleService scheduleService) {
        this.shiftRequestRepository = shiftRequestRepository;
        this.shiftRequestValidator = shiftRequestValidator;
        this.scheduleService = scheduleService;
    }

    @Override
    public ShiftRequest createShiftRequest(ShiftRequest shiftRequest) {
        validateShiftRequest(shiftRequest);
        shiftRequest.setStatus(ShiftRequestStatus.PENDING);
        return shiftRequestRepository.save(shiftRequest);
    }

    /**
     * When a ShiftRequest has been approved:
     * <li>1. It needs to be saved in the ShiftRequest DB.
     * <li>2. Update the schedule to include the approved ShiftRequest, which is now a ShiftEntry.
     */
    @Override
    public ShiftRequest approveShiftRequest(Long employeeId, Long shiftRequestId)
            throws ShiftRequestNotFoundException {

        ShiftRequest shiftRequest =
                getShiftRequestByIdAndStatus(shiftRequestId, ShiftRequestStatus.PENDING);
        shiftRequest.setStatus(ShiftRequestStatus.APPROVED);
        ShiftRequest approvedShiftRequest = shiftRequestRepository.save(shiftRequest);

        scheduleService.addShiftToSchedule(employeeId, approvedShiftRequest);

        return approvedShiftRequest;
    }

    @Override
    public ShiftRequest rejectShiftRequest(Long shiftRequestId, String rejectionReason)
            throws ShiftRequestNotFoundException {
        ShiftRequest shiftRequest =
                getShiftRequestByIdAndStatus(shiftRequestId, ShiftRequestStatus.PENDING);

        // TODO: validation?
        shiftRequest.setStatus(ShiftRequestStatus.REJECTED);
        shiftRequest.setRejectionReason(rejectionReason);
        return shiftRequestRepository.save(shiftRequest);
    }

    @Override
    public ShiftRequest getShiftRequestByIdAndStatus(Long shiftRequestId, ShiftRequestStatus status)
            throws ShiftRequestNotFoundException {
        return shiftRequestRepository
                .findByIdAndStatus(shiftRequestId, status)
                .orElseThrow(
                        () ->
                                new ShiftRequestNotFoundException(
                                        "Could not find pending shift request with ID: " + shiftRequestId));
    }

    @Override
    public List<ShiftRequest> getShiftRequestsByEmployee(Long employeeId) {
        return shiftRequestRepository.findByEmployeeId(employeeId);
    }

    @Override
    public List<ShiftRequest> getShiftRequestsByDateRange(
            LocalDateTime startDate, LocalDateTime endDate) {
        return shiftRequestRepository.findByDateRange(startDate, endDate);
    }

    private void validateShiftRequest(ShiftRequest shiftRequest) {
        shiftRequestValidator.validateShiftRequest(shiftRequest, shiftRequestRepository);
    }
}
