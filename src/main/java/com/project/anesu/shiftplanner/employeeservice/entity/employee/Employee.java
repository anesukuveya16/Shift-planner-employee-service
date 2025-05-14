package com.project.anesu.shiftplanner.employeeservice.entity.employee;

import com.project.anesu.shiftplanner.employeeservice.entity.schedule.Schedule;
import com.project.anesu.shiftplanner.employeeservice.entity.vacation.VacationRequest;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(updatable = false, unique = true, nullable = false)
  private UUID personUuid;

  @Column(updatable = false, nullable = false)
  private LocalDate birthDate;

  private String firstName;
  private String lastName;
  private String phone;
  private String address;
  private String email;

  @OneToOne(
      mappedBy = "employee",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private Schedule schedule;

  @OneToMany(
      mappedBy = "employee",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private List<VacationRequest> vacationRequests;
}
