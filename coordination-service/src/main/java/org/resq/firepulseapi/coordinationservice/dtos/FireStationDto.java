package org.resq.firepulseapi.coordinationservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FireStationDto {
    private String id;
    private Instant createdAt;
    private Instant updatedAt;
    private String name;
    private Double latitude;
    private Double longitude;
}
