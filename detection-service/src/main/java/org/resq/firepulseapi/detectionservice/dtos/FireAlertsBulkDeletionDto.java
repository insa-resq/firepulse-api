package org.resq.firepulseapi.detectionservice.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FireAlertsBulkDeletionDto {
    @Size(min = 1, message = "At least one fire alert ID must be provided")
    @NotNull(message = "Fire alert IDs list is required")
    private List<Integer> fireAlertIds;
}
