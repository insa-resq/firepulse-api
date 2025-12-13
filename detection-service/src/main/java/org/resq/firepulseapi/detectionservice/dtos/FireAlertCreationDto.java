package org.resq.firepulseapi.detectionservice.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.resq.firepulseapi.detectionservice.entities.enums.FireSeverity;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FireAlertCreationDto {
    @NotBlank(message = "Description cannot be blank")
    @NotNull(message = "Description is required")
    private String description;

    @Min(value = 0, message = "Confidence must be at least 0")
    @Max(value = 1, message = "Confidence must be at most 1")
    @NotNull(message = "Confidence is required")
    private Double confidence;

    @Min(value = -90, message = "Latitude must be at least -90")
    @Max(value = 90, message = "Latitude must be at most 90")
    @NotNull(message = "Latitude is required")
    private Double latitude;

    @Min(value = -180, message = "Longitude must be at least -180")
    @Max(value = 180, message = "Longitude must be at most 180")
    @NotNull(message = "Longitude is required")
    private Double longitude;

    @NotNull(message = "Fire severity is required")
    private FireSeverity severity;

    @NotBlank(message = "Image ID cannot be blank")
    @NotNull(message = "Image ID is required")
    private String imageId;
}
