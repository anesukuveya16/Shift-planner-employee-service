package com.project.anesu.shiftplanner.employeeservice.unitTests.util;

import com.project.anesu.shiftplanner.employeeservice.entity.employee.Employee;
import com.project.anesu.shiftplanner.employeeservice.entity.vacation.VacationRequest;
import com.project.anesu.shiftplanner.employeeservice.entity.vacation.VacationRequestStatus;
import com.project.anesu.shiftplanner.employeeservice.model.repository.VacationRequestRepository;
import com.project.anesu.shiftplanner.employeeservice.service.exception.InvalidVacationRequestException;
import com.project.anesu.shiftplanner.employeeservice.service.util.VacationRequestValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VacationRequestValidatorTest {
    @Mock
    private VacationRequestRepository vacationRequestRepositoryMock;

    private Employee employee;
    private VacationRequestValidator cut;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setFirstName("Marge");
        employee.setLastName("Simpson");
        employee.setId(1L);
        cut = new VacationRequestValidator();
    }

    @Test
    void shouldThrowExceptionWhenVacationLimitExceededWithAllVacationsInCurrentYear() {
        // Given
        List<VacationRequest> usedVacationDays = createVacationRequests(20, 10);

        VacationRequest givenVacationRequest = createVacationRequest(10);

        when(vacationRequestRepositoryMock.findByEmployeeIdAndYearOverlap(anyLong(), any(), any()))
                .thenReturn(usedVacationDays);

        // When & Then
        assertThrows(
                InvalidVacationRequestException.class,
                () -> cut.validateVacationRequest(givenVacationRequest, vacationRequestRepositoryMock));
    }

    @Test
    void shouldNotThrowExceptionWhenSpanningPreviousYearWithinLimit() {
        // Given
        List<VacationRequest> usedVacationDays =
                createVacationRequestsWithPreviousYearSpanningFiveDaysIntoCurrentYear();

        VacationRequest givenVacationRequest = createVacationRequest(20);

        when(vacationRequestRepositoryMock.findByEmployeeIdAndYearOverlap(anyLong(), any(), any()))
                .thenReturn(usedVacationDays);

        // When & Then
        cut.validateVacationRequest(givenVacationRequest, vacationRequestRepositoryMock);
    }

    @Test
    void shouldThrowExceptionWhenSpanningPreviousYearExceedsLimit() {
        // Given
        List<VacationRequest> usedVacationDays =
                createVacationRequestsWithPreviousYearSpanningFiveDaysIntoCurrentYear();

        VacationRequest givenVacationRequest = createVacationRequest(30);

        when(vacationRequestRepositoryMock.findByEmployeeIdAndYearOverlap(anyLong(), any(), any()))
                .thenReturn(usedVacationDays);

        // When & Then
        assertThrows(
                InvalidVacationRequestException.class,
                () -> cut.validateVacationRequest(givenVacationRequest, vacationRequestRepositoryMock));
    }

    @Test
    void shouldNotThrowExceptionWhenVacationWithinLimit() {
        // Given
        List<VacationRequest> usedVacationDays = createVacationRequests(10);

        VacationRequest givenVacationRequest = createVacationRequest(10);

        when(vacationRequestRepositoryMock.findByEmployeeIdAndYearOverlap(anyLong(), any(), any()))
                .thenReturn(usedVacationDays);

        // When & Then
        cut.validateVacationRequest(givenVacationRequest, vacationRequestRepositoryMock);
    }

    @Test
    void shouldThrowExceptionForRealisticMultiVacationScenarioExceedingLimit() {
        // Given
        List<VacationRequest> usedVacationDays = createRealisticVacationScenario();

        VacationRequest newVacationRequest = createVacationRequest(12);

        when(vacationRequestRepositoryMock.findByEmployeeIdAndYearOverlap(anyLong(), any(), any()))
                .thenReturn(usedVacationDays);

        // When & Then
        assertThrows(
                InvalidVacationRequestException.class,
                () -> cut.validateVacationRequest(newVacationRequest, vacationRequestRepositoryMock));
    }

    @Test
    void shouldNotThrowExceptionWhenRemainingVacationDaysMatchNewRequestSpanningIntoNextYear() {
        // Given
        List<VacationRequest> usedVacationDays = createRealisticVacationScenario();

        VacationRequest newVacationRequest = createVacationRequestSpanningIntoNextYear(7);

        when(vacationRequestRepositoryMock.findByEmployeeIdAndYearOverlap(anyLong(), any(), any()))
                .thenReturn(usedVacationDays);

        // When & Then
        cut.validateVacationRequest(newVacationRequest, vacationRequestRepositoryMock);
    }

    // Helper Methods
    private List<VacationRequest> createVacationRequests(int... durations) {
        LocalDateTime start = LocalDateTime.of(LocalDate.now().getYear(), 1, 1, 0, 0);
        return Arrays.stream(durations)
                .mapToObj(duration -> createVacationRequestWithDuration(start, duration))
                .toList();
    }

    private List<VacationRequest>
    createVacationRequestsWithPreviousYearSpanningFiveDaysIntoCurrentYear() {
        LocalDateTime startPreviousYear = LocalDateTime.of(LocalDate.now().getYear() - 1, 12, 20, 0, 0);
        return List.of(createVacationRequestWithDuration(startPreviousYear, 15));
    }

    private List<VacationRequest> createRealisticVacationScenario() {
        return List.of(
                createVacationRequestWithDuration(
                        LocalDateTime.of(LocalDate.now().getYear() - 1, 12, 25, 0, 0), 15),
                createVacationRequestWithDuration(
                        LocalDateTime.of(LocalDate.now().getYear(), 3, 1, 0, 0), 10),
                createVacationRequestWithDuration(
                        LocalDateTime.of(LocalDate.now().getYear(), 6, 15, 0, 0), 5));
    }

    private VacationRequest createVacationRequest(int days) {
        return createVacationRequestWithDuration(
                LocalDateTime.of(LocalDate.now().getYear(), 11, 1, 0, 0), days);
    }

    private VacationRequest createVacationRequestSpanningIntoNextYear(int days) {
        return createVacationRequestWithDuration(
                LocalDateTime.of(LocalDate.now().getYear(), 12, 26, 0, 0), days);
    }

    private VacationRequest createVacationRequestWithDuration(LocalDateTime startDate, int days) {
        VacationRequest vacationRequest = new VacationRequest();
        vacationRequest.setEmployee(employee);
        vacationRequest.setStartDate(startDate);
        vacationRequest.setEndDate(startDate.plusDays(days - 1));
        vacationRequest.setStatus(VacationRequestStatus.PENDING);
        return vacationRequest;
    }
}
