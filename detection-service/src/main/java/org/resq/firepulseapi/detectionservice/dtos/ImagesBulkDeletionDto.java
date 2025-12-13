package org.resq.firepulseapi.detectionservice.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImagesBulkDeletionDto {
    @Size(min = 1, message = "At least one image ID must be provided")
    @NotNull(message = "Image IDs list is required")
    private List<@NotBlank(message = "An image ID cannot be blank") String> imageIds;
}
