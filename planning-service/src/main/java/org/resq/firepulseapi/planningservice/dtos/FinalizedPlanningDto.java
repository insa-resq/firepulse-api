package org.resq.firepulseapi.planningservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FinalizedPlanningDto {
    private PlanningDto planning;
    private List<ShiftAssignmentDto> shiftAssignments;
    private List<VehicleAvailabilityDto> vehicleAvailabilities;
}
