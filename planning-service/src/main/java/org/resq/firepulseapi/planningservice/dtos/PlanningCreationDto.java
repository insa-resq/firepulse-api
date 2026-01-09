package org.resq.firepulseapi.planningservice.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanningCreationDto {
    @NotNull(message = "Year is required")
    private Integer year;

    @NotNull(message = "Week number is required")
    private Integer weekNumber;

    @NotNull(message = "Station ID is required")
    private String stationId;
}
