package org.resq.firepulseapi.detectionservice.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.resq.firepulseapi.detectionservice.entities.enums.AlertStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FireAlertStatusUpdateDto {
    @NotNull(message = "Alert status is required")
    private AlertStatus status;
}
