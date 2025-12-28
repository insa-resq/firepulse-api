// File: PlanningController.java
package org.resq.firepulseapi.registryservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.resq.firepulseapi.registryservice.dtos.OverviewResponse;
import org.resq.firepulseapi.registryservice.repositories.FireStationRepository;
import org.resq.firepulseapi.registryservice.repositories.FirefighterRepository;
import org.resq.firepulseapi.registryservice.repositories.VehicleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/planning")
@Tag(name = "Planning Controller", description = "Planning overview endpoints")
@RequiredArgsConstructor
public class PlanningController {

    private final FireStationRepository fireStationRepository;
    private final FirefighterRepository firefighterRepository;
    private final VehicleRepository vehicleRepository;

    @GetMapping("/overview")
    @Operation(summary = "Get overview of staff and material across all fire stations")
    public ResponseEntity<OverviewResponse> getOverview() {
        // Get counts using repository count() method (efficient)
        int totalStations = (int) fireStationRepository.count();
        int totalFirefighters = (int) firefighterRepository.count();
        int totalVehicles = (int) vehicleRepository.count();

        // Build station summaries
        var stations = fireStationRepository.findAll().stream()
                .map(station -> {
                    // Count firefighters at this station
                    int firefighterCount = firefighterRepository.findAll().stream()
                            .filter(ff -> ff.getStation() != null &&
                                    ff.getStation().getId().equals(station.getId()))
                            .toList()
                            .size();

                    // Count vehicles at this station
                    int vehicleCount = vehicleRepository.findAll().stream()
                            .filter(v -> v.getStation() != null &&
                                    v.getStation().getId().equals(station.getId()))
                            .toList()
                            .size();

                    return new OverviewResponse.StationSummary(
                            station.getId(),
                            station.getName(),
                            firefighterCount,
                            vehicleCount
                    );
                })
                .toList();

        // Create and return the response
        OverviewResponse response = new OverviewResponse(
                Instant.now(),
                totalStations,
                totalFirefighters,
                totalVehicles,
                stations
        );

        return ResponseEntity.ok(response);
    }
}