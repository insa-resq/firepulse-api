package org.resq.firepulseapi.detectionservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.resq.firepulseapi.detectionservice.entities.Image;
import org.resq.firepulseapi.detectionservice.entities.enums.ImageSplit;
import org.resq.firepulseapi.detectionservice.utils.Conversion;

import java.time.Instant;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageDto {
    private String id;
    private Instant createdAt;
    private Instant updatedAt;
    private String url;
    private ImageSplit imageSplit;
    private Boolean containsFire;
    private Map<String, Object> metadata;

    public static ImageDto fromEntity(Image image) {
        ImageDto dto = new ImageDto();
        dto.setId(image.getId());
        dto.setCreatedAt(image.getCreatedAt());
        dto.setUpdatedAt(image.getUpdatedAt());
        dto.setUrl(image.getUrl());
        dto.setImageSplit(image.getSplit());
        dto.setContainsFire(image.getContainsFire());
        dto.setMetadata(Conversion.jsonNodeToMap(image.getMetadata()));
        return dto;
    }
}
