package org.resq.firepulseapi.registryservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.resq.firepulseapi.registryservice.entities.enums.VehicleType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleFilters {
    private String stationId;
    private VehicleType type;
}