package org.resq.firepulseapi.registryservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OverviewResponse {
    private Instant timestamp;
    private int totalStations;
    private int totalFirefighters;
    private int totalVehicles;
    private List<StationSummary> stations;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StationSummary {
        private String id;
        private String name;
        private int firefighterCount;
        private int vehicleCount;
    }
}