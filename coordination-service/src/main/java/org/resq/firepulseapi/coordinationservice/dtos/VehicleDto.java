package org.resq.firepulseapi.coordinationservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.resq.firepulseapi.coordinationservice.entities.enums.VehicleType;

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
    private Integer bookedCount;
    private String stationId;
}
