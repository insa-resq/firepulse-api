package org.resq.firepulseapi.planningservice.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvailabilitySlotUpdateDto {
    @NotNull(message = "Availability status is required")
    private Boolean isAvailable;
}
