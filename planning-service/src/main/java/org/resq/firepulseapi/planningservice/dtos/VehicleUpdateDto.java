package org.resq.firepulseapi.planningservice.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleUpdateDto {
    @NotNull(message = "Vehicle ID is required")
    private String vehicleId;
    private Integer totalCount;
    private Integer availableCount;
}
