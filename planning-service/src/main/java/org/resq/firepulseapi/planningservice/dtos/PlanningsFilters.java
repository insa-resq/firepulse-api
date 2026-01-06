package org.resq.firepulseapi.planningservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanningsFilters {
    private Integer year;
    private Integer weekNumber;
    private String stationId;
}
