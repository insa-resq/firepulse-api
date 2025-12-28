// VehicleDto.java - UPDATED
package org.resq.firepulseapi.registryservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.resq.firepulseapi.registryservice.entities.Vehicle;
import org.resq.firepulseapi.registryservice.entities.enums.VehicleType;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleDto {
    private String id;
    private Instant createdAt;
    private Instant updatedAt;
    private VehicleType type;
    private Integer totalCount;
    private Integer availableCount;
    private String stationId;

    public static VehicleDto fromEntity(Vehicle vehicle) {
        if (vehicle == null) return null;
        VehicleDto dto = new VehicleDto();
        dto.setId(vehicle.getId());
        dto.setCreatedAt(vehicle.getCreatedAt());
        dto.setUpdatedAt(vehicle.getUpdatedAt());
        dto.setType(vehicle.getType());
        dto.setTotalCount(vehicle.getTotalCount());
        dto.setAvailableCount(vehicle.getAvailableCount());
        dto.setStationId(vehicle.getStation() != null ? vehicle.getStation().getId() : null);
        return dto;
    }
}