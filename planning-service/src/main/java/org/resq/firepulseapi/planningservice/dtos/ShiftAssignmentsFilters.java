package org.resq.firepulseapi.planningservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.resq.firepulseapi.planningservice.entities.enums.ShiftType;
import org.resq.firepulseapi.planningservice.entities.enums.Weekday;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShiftAssignmentsFilters {
    private Weekday weekday;
    private ShiftType shiftType;
    private String firefighterId;
    private String planningId;
}
