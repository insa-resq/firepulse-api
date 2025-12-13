package org.resq.firepulseapi.detectionservice.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.resq.firepulseapi.detectionservice.entities.enums.ImageSplit;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImagesBulkCreationDto {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ImageCreationDto {
        @NotNull(message = "URL is required")
        private String url;
        @NotNull(message = "Width is required")
        private Integer width;
        @NotNull(message = "Height is required")
        private Integer height;
        @NotNull(message = "Split is required")
        private ImageSplit split;
        private Map<String, Object> metadata;
    }

    @Size(min = 1, max = 10_000, message = "Images list must contain between 1 and 10000 items")
    @NotNull(message = "Images list is required")
    private List<@Valid ImageCreationDto> images;
}
