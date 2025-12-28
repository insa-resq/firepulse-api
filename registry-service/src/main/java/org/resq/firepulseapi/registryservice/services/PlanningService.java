// PlanningService.java - NEW FILE
package org.resq.firepulseapi.registryservice.services;

import lombok.RequiredArgsConstructor;
import org.resq.firepulseapi.registryservice.dtos.OverviewResponse;
import org.resq.firepulseapi.registryservice.repositories.FireStationRepository;
import org.resq.firepulseapi.registryservice.repositories.FirefighterRepository;
import org.resq.firepulseapi.registryservice.repositories.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlanningService {

    private final FireStationRepository fireStationRepository;
    private final FirefighterRepository firefighterRepository;
    private final VehicleRepository vehicleRepository;

    public OverviewResponse getOverview() {
        // Get all data with proper joins
        var stations = fireStationRepository.findAll();
        var firefighters = firefighterRepository.findAll();
        var vehicles = vehicleRepository.findAll();

        // Group counts by station
        Map<String, Long> firefightersByStation = firefighters.stream()
                .filter(ff -> ff.getStation() != null)
                .collect(Collectors.groupingBy(
                        ff -> ff.getStation().getId(),
                        Collectors.counting()
                ));

        Map<String, Long> vehiclesByStation = vehicles.stream()
                .filter(v -> v.getStation() != null)
                .collect(Collectors.groupingBy(
                        v -> v.getStation().getId(),
                        Collectors.counting()
                ));

        // Build station summaries
        var stationSummaries = stations.stream()
                .map(station -> new OverviewResponse.StationSummary(
                        station.getId(),
                        station.getName(),
                        Math.toIntExact(firefightersByStation.getOrDefault(station.getId(), 0L)),
                        Math.toIntExact(vehiclesByStation.getOrDefault(station.getId(), 0L))
                ))
                .toList();

        return new OverviewResponse(
                Instant.now(),
                stations.size(),
                firefighters.size(),
                vehicles.size(),
                stationSummaries
        );
    }
}