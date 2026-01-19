package org.resq.firepulseapi.planningservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.resq.firepulseapi.planningservice.entities.VehicleAvailability;
import org.resq.firepulseapi.planningservice.entities.enums.Weekday;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleAvailabilityDto {
    private String id;
    private Instant createdAt;
    private Instant updatedAt;
    private Weekday weekday;
    private Integer availableCount;
    private Integer bookedCount;
    private String vehicleId;

    public static VehicleAvailabilityDto fromEntity(VehicleAvailability vehicleAvailability) {
        VehicleAvailabilityDto dto = new VehicleAvailabilityDto();
        dto.setId(vehicleAvailability.getId());
        dto.setCreatedAt(vehicleAvailability.getCreatedAt());
        dto.setUpdatedAt(vehicleAvailability.getUpdatedAt());
        dto.setWeekday(vehicleAvailability.getWeekday());
        dto.setAvailableCount(vehicleAvailability.getAvailableCount());
        dto.setBookedCount(vehicleAvailability.getBookedCount());
        dto.setVehicleId(vehicleAvailability.getVehicleId());
        return dto;
    }
}
