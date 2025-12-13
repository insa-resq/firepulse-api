package org.resq.firepulseapi.detectionservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.resq.firepulseapi.detectionservice.entities.FireAlert;
import org.resq.firepulseapi.detectionservice.entities.enums.AlertStatus;
import org.resq.firepulseapi.detectionservice.entities.enums.FireSeverity;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FireAlertDto {
    private Integer id;
    private Instant createdAt;
    private Instant updatedAt;
    private String description;
    private Double confidence;
    private Double latitude;
    private Double longitude;
    private FireSeverity severity;
    private AlertStatus status;
    private String imageId;

    public static FireAlertDto fromEntity(FireAlert fireAlert) {
        FireAlertDto dto = new FireAlertDto();
        dto.setId(fireAlert.getId());
        dto.setCreatedAt(fireAlert.getCreatedAt());
        dto.setUpdatedAt(fireAlert.getUpdatedAt());
        dto.setDescription(fireAlert.getDescription());
        dto.setConfidence(fireAlert.getConfidence());
        dto.setLatitude(fireAlert.getLatitude());
        dto.setLongitude(fireAlert.getLongitude());
        dto.setSeverity(fireAlert.getSeverity());
        dto.setStatus(fireAlert.getStatus());
        dto.setImageId(fireAlert.getImage().getId());
        return dto;
    }
}
