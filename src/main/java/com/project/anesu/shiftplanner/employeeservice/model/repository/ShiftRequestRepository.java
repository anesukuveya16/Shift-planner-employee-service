package com.project.anesu.shiftplanner.employeeservice.model.repository;

import com.project.anesu.shiftplanner.employeeservice.entity.shift.ShiftRequest;
import com.project.anesu.shiftplanner.employeeservice.entity.shift.ShiftRequestStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ShiftRequestRepository extends JpaRepository<ShiftRequest, Long> {

  List<ShiftRequest> findByEmployeeId(Long employeeId);

  @Query(
      "SELECT s FROM ShiftRequest s WHERE s.shiftDate >= :startOfWeek AND s.shiftDate <= :endOfWeek")
  List<ShiftRequest> findByDateRange(
      @Param("startOfWeek") LocalDateTime startDate, @Param("endOfWeek") LocalDateTime endDate);

  Optional<ShiftRequest> findByEmployeeIdAndShiftDate(Long employeeId, LocalDateTime shiftDate);

  Optional<ShiftRequest> findByIdAndStatus(Long shiftRequestId, ShiftRequestStatus status);
}
