package com.project.anesu.shiftplanner.employeeservice.entity.vacation;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.project.anesu.shiftplanner.employeeservice.entity.employee.Employee;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VacationRequest {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @JsonBackReference
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "employee_id", nullable = false)
  private Employee employee;

  private Long officeLocationId;
  private LocalDateTime startDate;
  private LocalDateTime endDate;

  @Enumerated(EnumType.STRING)
  private VacationRequestStatus status;

  private String rejectionReason;
}
