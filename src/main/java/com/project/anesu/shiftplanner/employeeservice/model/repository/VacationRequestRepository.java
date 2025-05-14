package com.project.anesu.shiftplanner.employeeservice.model.repository;

import com.project.anesu.shiftplanner.employeeservice.entity.employee.Employee;
import com.project.anesu.shiftplanner.employeeservice.entity.vacation.VacationRequest;
import com.project.anesu.shiftplanner.employeeservice.entity.vacation.VacationRequestStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VacationRequestRepository extends JpaRepository<VacationRequest, Long> {

  @Query("SELECT e FROM Employee e JOIN FETCH e.vacationRequests WHERE e.id = :id")
  Employee findEmployeeWithVacationRequests(@Param("id") Long employeeId);

  @Query("SELECT vr FROM VacationRequest vr WHERE vr.employee.id IN :employeeIds")
  List<VacationRequest> findVacationRequestsByEmployeeIds(
      @Param("employeeIds") List<Long> employeeIds);

  List<VacationRequest> findByEmployeeId(Long employeeId);

  @Query("SELECT v FROM VacationRequest v WHERE v.employee.id = :employeeId AND v.startDate >= :startOfWeek AND v.endDate <= :endOfWeek")
  List<VacationRequest> findByEmployeeIdAndDateRange(
          @Param("employeeId") Long employeeId,
          @Param("startOfWeek") LocalDateTime startOfWeek,
          @Param("endOfWeek") LocalDateTime endOfWeek);

  @Query("SELECT v FROM VacationRequest v WHERE v.officeLocationId = :officeLocationId " +
          "AND v.startDate <= :endDate AND v.endDate >= :startDate " +
          "AND v.status IN :approvedStatuses")
  List<VacationRequest> findByOfficeLocationAndDateRangeAndStatus(
          @Param("officeLocationId") Long officeLocationId,
          @Param("startDate") LocalDateTime startDate,
          @Param("endDate") LocalDateTime endDate,
          @Param("approvedStatuses") List<VacationRequestStatus> approvedStatuses);

  @Query(
      "SELECT v FROM VacationRequest v WHERE v.employee.id = :employeeId "
          + "AND (v.startDate <= :endOfYear AND v.endDate >= :startOfYear)")
  List<VacationRequest> findByEmployeeIdAndYearOverlap(
      @Param("employeeId") Long employeeId,
      @Param("startOfYear") LocalDate startOfYear,
      @Param("endOfYear") LocalDate endOfYear);
}

