package org.resq.firepulseapi.planningservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.resq.firepulseapi.planningservice.entities.ShiftAssignment;
import org.resq.firepulseapi.planningservice.entities.enums.ShiftType;
import org.resq.firepulseapi.planningservice.entities.enums.Weekday;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShiftAssignmentDto {
    private String id;
    private Instant createdAt;
    private Instant updatedAt;
    private Weekday weekday;
    private ShiftType shiftType;
    private String firefighterId;
    private String planningId;

    public static ShiftAssignmentDto fromEntity(ShiftAssignment assignment) {
        ShiftAssignmentDto dto = new ShiftAssignmentDto();
        dto.setId(assignment.getId());
        dto.setCreatedAt(assignment.getCreatedAt());
        dto.setUpdatedAt(assignment.getUpdatedAt());
        dto.setWeekday(assignment.getWeekday());
        dto.setShiftType(assignment.getShiftType());
        dto.setFirefighterId(assignment.getFirefighterId());
        dto.setPlanningId(assignment.getPlanning().getId());
        return dto;
    }
}
