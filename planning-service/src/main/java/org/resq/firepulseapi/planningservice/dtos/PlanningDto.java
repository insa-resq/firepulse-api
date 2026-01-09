package org.resq.firepulseapi.planningservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.resq.firepulseapi.planningservice.entities.Planning;
import org.resq.firepulseapi.planningservice.entities.enums.PlanningStatus;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanningDto {
    private String id;
    private Instant createdAt;
    private Instant updatedAt;
    private Integer year;
    private Integer weekNumber;
    private PlanningStatus status;
    private String stationId;

    public static PlanningDto fromEntity(Planning planning) {
        PlanningDto dto = new PlanningDto();
        dto.setId(planning.getId());
        dto.setCreatedAt(planning.getCreatedAt());
        dto.setUpdatedAt(planning.getUpdatedAt());
        dto.setYear(planning.getYear());
        dto.setWeekNumber(planning.getWeekNumber());
        dto.setStatus(planning.getStatus());
        dto.setStationId(planning.getStationId());
        return dto;
    }
}
