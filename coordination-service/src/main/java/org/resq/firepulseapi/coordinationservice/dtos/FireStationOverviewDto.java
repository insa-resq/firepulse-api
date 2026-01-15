package org.resq.firepulseapi.coordinationservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.resq.firepulseapi.coordinationservice.entities.enums.VehicleType;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FireStationOverviewDto {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AvailableVehicleDto {
        private VehicleType type;
        private Integer count;
    }

    private List<AvailableVehicleDto> availableVehicles;
}
