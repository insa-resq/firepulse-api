package org.resq.firepulseapi.planningservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.resq.firepulseapi.planningservice.entities.enums.Weekday;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleAvailabilitiesFilters {
    private Weekday weekday;
    private List<String> vehicleIds;
}
