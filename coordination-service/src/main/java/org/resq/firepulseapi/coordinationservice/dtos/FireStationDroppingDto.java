package org.resq.firepulseapi.coordinationservice.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.resq.firepulseapi.coordinationservice.entities.enums.VehicleType;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FireStationDroppingDto {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VehicleDroppingDto {
        @NotNull(message = "Vehicle type is required")
        private VehicleType type;

        @Min(value = 1, message = "Dropped count must be at least 1")
        @NotNull(message = "Dropped count is required")
        private Integer droppedCount;
    }

    @NotNull(message = "Station ID is required")
    private String stationId;

    @Size(min = 1, message = "At least one vehicle dropping is required")
    @NotNull(message = "Vehicles list is required")
    private List<@Valid VehicleDroppingDto> vehicles;
}
