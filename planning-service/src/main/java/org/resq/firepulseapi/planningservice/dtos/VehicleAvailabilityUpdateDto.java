package org.resq.firepulseapi.planningservice.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleAvailabilityUpdateDto {
    @Min(value = 0, message = "Booked count must be non-negative")
    private Integer bookedCount;

    @NotNull(message = "Availability ID is required")
    private String availabilityId;
}
