package org.resq.firepulseapi.detectionservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.resq.firepulseapi.detectionservice.entities.enums.ImageSplit;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImagesFilters {
    private Set<ImageSplit> splits;
    private Boolean containsFire;
}
