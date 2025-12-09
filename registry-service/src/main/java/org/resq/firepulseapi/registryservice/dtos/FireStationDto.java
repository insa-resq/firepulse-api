package org.resq.firepulseapi.registryservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.resq.firepulseapi.registryservice.entities.FireStation;

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

    public static FireStationDto fromEntity(FireStation station) {
        FireStationDto dto = new FireStationDto();
        dto.setId(station.getId());
        dto.setCreatedAt(station.getCreatedAt());
        dto.setUpdatedAt(station.getUpdatedAt());
        dto.setName(station.getName());
        dto.setLatitude(station.getLatitude());
        dto.setLongitude(station.getLongitude());
        return dto;
    }
}
