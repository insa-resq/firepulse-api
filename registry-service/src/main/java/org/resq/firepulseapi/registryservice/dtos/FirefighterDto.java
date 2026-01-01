package org.resq.firepulseapi.registryservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.resq.firepulseapi.registryservice.entities.Firefighter;
import org.resq.firepulseapi.registryservice.entities.enums.FirefighterRank;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FirefighterDto {
    private String id;
    private Instant createdAt;
    private Instant updatedAt;
    private String firstName;
    private String lastName;
    private FirefighterRank rank;
    private String userId;
    private String stationId;

    public static FirefighterDto fromEntity(Firefighter firefighter) {
        FirefighterDto dto = new FirefighterDto();
        dto.setId(firefighter.getId());
        dto.setCreatedAt(firefighter.getCreatedAt());
        dto.setUpdatedAt(firefighter.getUpdatedAt());
        dto.setFirstName(firefighter.getFirstName());
        dto.setLastName(firefighter.getLastName());
        dto.setRank(firefighter.getRank());
        dto.setUserId(firefighter.getUserId());
        dto.setStationId(firefighter.getStation().getId());
        return dto;
    }
}