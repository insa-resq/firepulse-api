package org.resq.firepulseapi.planningservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.resq.firepulseapi.planningservice.entities.enums.ShiftType;
import org.resq.firepulseapi.planningservice.entities.enums.Weekday;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetailedShiftAssignmentDto {
    private String id;
    private Instant createdAt;
    private Instant updatedAt;
    private Weekday weekday;
    private ShiftType shiftType;
    private String planningId;
    private FirefighterDto firefighter;
}
