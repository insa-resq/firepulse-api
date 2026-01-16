package org.resq.firepulseapi.planningservice.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.resq.firepulseapi.planningservice.entities.enums.ShiftType;
import org.resq.firepulseapi.planningservice.entities.enums.Weekday;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanningFinalizationDto {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ShiftAssignmentCreationDto {
        private Weekday weekday;
        private ShiftType shiftType;
        private String firefighterId;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VehicleAvailabilityDto {
        private String vehicleId;
        @PositiveOrZero(message = "Available count must be zero or positive")
        private Integer availableCount;
    }

    @Size(min = 1, message = "At least one shift assignment is required")
    private List<@Valid ShiftAssignmentCreationDto> shiftAssignments;

    @Size(min = 1, message = "At least one vehicle availability is required")
    private List<@Valid VehicleAvailabilityDto> vehicleAvailabilities;
}
