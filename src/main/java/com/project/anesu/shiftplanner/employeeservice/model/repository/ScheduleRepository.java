package com.project.anesu.shiftplanner.employeeservice.model.repository;

import com.project.anesu.shiftplanner.employeeservice.entity.schedule.Schedule;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

  /**
   * Finds schedules for a specific employee within a date range.
   *
   * @param employeeId the ID of the employee
   * @param startDate the start of the date range
   * @param endDate the end of the date range
   * @return a list of matching schedules
   */
  @Query(
      "SELECT s FROM Schedule s WHERE s.employee.id = :employeeId AND s.startDate >= :startOfWeek AND s.endDate <= :endOfWeek")
  Optional<List<Schedule>> findByEmployeeIdAndDateRange(
      @Param("employeeId") Long employeeId,
      @Param("startOfWeek") LocalDateTime startDate,
      @Param("endOfWeek") LocalDateTime endDate);

  /**
   * Finds schedules for a specific employee within a given calendar week.
   *
   * @param employeeId the ID of the employee
   * @param startOfCalenderWeek the start of the date range
   * @param endOfCalenderWeek the end of the date range
   * @return a matching schedule.
   */
  @Query(
      "SELECT s FROM Schedule s WHERE s.employee.id = :employeeId AND s.startDate >= :startOfCalendarWeek AND s.endDate <= :endOfCalendarWeek")
  Optional<Schedule> findByEmployeeIdAndCalendarWeek(
      @Param("employeeId") Long employeeId,
      @Param("startOfCalendarWeek") LocalDateTime startOfCalenderWeek,
      @Param("endOfCalendarWeek") LocalDateTime endOfCalenderWeek);
}
