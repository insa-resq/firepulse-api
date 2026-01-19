package org.resq.firepulseapi.coordinationservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.resq.firepulseapi.coordinationservice.entities.enums.Weekday;

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
}
