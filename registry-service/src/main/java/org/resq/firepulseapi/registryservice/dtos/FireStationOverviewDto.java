package org.resq.firepulseapi.registryservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.resq.firepulseapi.registryservice.entities.Vehicle;
import org.resq.firepulseapi.registryservice.entities.enums.VehicleType;

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

        public static AvailableVehicleDto fromEntity(Vehicle vehicle) {
            AvailableVehicleDto dto = new AvailableVehicleDto();
            dto.setType(vehicle.getType());
            dto.setCount(vehicle.getAvailableCount());
            return dto;
        }
    }

    private List<AvailableVehicleDto> availableVehicles;
}
