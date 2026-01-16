package org.resq.firepulseapi.planningservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.resq.firepulseapi.planningservice.entities.enums.FirefighterRank;

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
}
