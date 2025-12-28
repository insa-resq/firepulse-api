package org.resq.firepulseapi.registryservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.resq.firepulseapi.registryservice.entities.FirefighterTraining;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FirefighterTrainingDto {
    private String id;
    private Instant createdAt;
    private Instant updatedAt;
    private Boolean permitB;
    private Boolean permitC;
    private Boolean permitAircraft;
    private Boolean suap;
    private Boolean inc;
    private Boolean smallTeamLeader;
    private Boolean mediumTeamLeader;
    private Boolean largeTeamLeader;
    private String firefighterId;

    public static FirefighterTrainingDto fromEntity(FirefighterTraining training) {
        FirefighterTrainingDto dto = new FirefighterTrainingDto();
        dto.setId(training.getId());
        dto.setCreatedAt(training.getCreatedAt());
        dto.setUpdatedAt(training.getUpdatedAt());
        dto.setPermitB(training.getPermitB());
        dto.setPermitC(training.getPermitC());
        dto.setPermitAircraft(training.getPermitAircraft());
        dto.setSuap(training.getSuap());
        dto.setInc(training.getInc());
        dto.setSmallTeamLeader(training.getSmallTeamLeader());
        dto.setMediumTeamLeader(training.getMediumTeamLeader());
        dto.setLargeTeamLeader(training.getLargeTeamLeader());
        dto.setFirefighterId(training.getFirefighter().getId());
        return dto;
    }
}