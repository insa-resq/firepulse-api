package org.resq.firepulseapi.planningservice.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
        @NotNull(message = "Firefighter ID is required")
        private String firefighterId;

        @NotNull(message = "Weekday and shift type are required")
        private Weekday weekday;

        @NotNull(message = "Shift type is required")
        private ShiftType shiftType;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VehicleAvailabilityCreationDto {
        @NotNull(message = "Vehicle ID is required")
        private String vehicleId;

        @NotNull(message = "Weekday and available count are required")
        private Weekday weekday;

        @PositiveOrZero(message = "Available count must be zero or positive")
        @NotNull(message = "Available count is required")
        private Integer availableCount;
    }

    @Size(min = 1, message = "At least one shift assignment is required")
    @NotNull(message = "Shift assignments list is required")
    private List<@Valid ShiftAssignmentCreationDto> shiftAssignments;

    @Size(min = 1, message = "At least one vehicle availability is required")
    @NotNull(message = "Vehicle availabilities list is required")
    private List<@Valid VehicleAvailabilityCreationDto> vehicleAvailabilities;
}
