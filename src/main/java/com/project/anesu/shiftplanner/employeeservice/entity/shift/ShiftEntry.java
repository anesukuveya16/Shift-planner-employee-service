package com.project.anesu.shiftplanner.employeeservice.entity.shift;

import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class ShiftEntry {

  private Long shiftId;
  private LocalDateTime shiftDate;
  private ShiftType shiftType;
  private Long workingHours;

  public static ShiftEntry fromApprovedShiftRequest(ShiftRequest approvedShiftRequest) {
    return builder()
        .shiftDate(approvedShiftRequest.getShiftDate())
        .shiftType(approvedShiftRequest.getShiftType())
        .workingHours(approvedShiftRequest.getShiftLengthInHours())
        .build();
  }
}
