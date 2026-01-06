package org.resq.firepulseapi.planningservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.resq.firepulseapi.planningservice.entities.enums.Weekday;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvailabilitySlotsFilters {
    private Integer year;
    private Integer weekNumber;
    private Weekday weekday;
    private String firefighterId;
}
