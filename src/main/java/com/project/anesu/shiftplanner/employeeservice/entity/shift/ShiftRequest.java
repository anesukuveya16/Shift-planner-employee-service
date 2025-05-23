package com.project.anesu.shiftplanner.employeeservice.entity.shift;

import com.project.anesu.shiftplanner.employeeservice.entity.employee.Employee;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShiftRequest {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "employee_id", nullable = false)
  private Employee employee;

  private LocalDateTime shiftDate;

  @Enumerated(EnumType.STRING)
  private ShiftRequestStatus status;

  private String rejectionReason;

  private Long shiftLengthInHours;

  private ShiftType shiftType;
}
