package org.resq.firepulseapi.registryservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.resq.firepulseapi.registryservice.entities.enums.FirefighterRank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FirefighterFilters {
    private String stationId;
    private FirefighterRank rank;
}