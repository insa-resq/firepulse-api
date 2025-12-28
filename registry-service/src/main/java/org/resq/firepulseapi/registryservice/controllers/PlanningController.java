// PlanningController.java - UPDATED
package org.resq.firepulseapi.registryservice.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.resq.firepulseapi.registryservice.dtos.OverviewResponse;
import org.resq.firepulseapi.registryservice.services.PlanningService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.resq.firepulseapi.registryservice.repositories.FireStationRepository;
import org.resq.firepulseapi.registryservice.repositories.FirefighterRepository;
import org.resq.firepulseapi.registryservice.repositories.VehicleRepository;

@RestController
@RequestMapping("/planning")
@Tag(name = "Planning Controller", description = "Planning overview endpoints")
@RequiredArgsConstructor
public class PlanningController {


    private final FireStationRepository fireStationRepository;
    private final FirefighterRepository firefighterRepository;
    private final VehicleRepository vehicleRepository;
    private final PlanningService planningService;

    @GetMapping("/overview")
    @Operation(summary = "Get overview of staff and material across all fire stations")
    public ResponseEntity<OverviewResponse> getOverview() {
        OverviewResponse response = planningService.getOverview();
        return ResponseEntity.ok(response);
    }

    // Add debug endpoint
    @GetMapping("/debug")
    @Operation(summary = "Debug endpoint to check data")
    public ResponseEntity<String> debug() {
        var response = planningService.getOverview();
        return ResponseEntity.ok("Total stations: " + response.getTotalStations() +
                "\nTotal firefighters: " + response.getTotalFirefighters() +
                "\nTotal vehicles: " + response.getTotalVehicles());
    }

    @GetMapping("/overview-map")
    public ResponseEntity<Map<String, Object>> getOverviewMap() {
        // FIX: Use instance variables (lowercase), not class names
        int totalStations = (int) fireStationRepository.count();  // lowercase f
        int totalFirefighters = (int) firefighterRepository.count();  // lowercase f
        int totalVehicles = (int) vehicleRepository.count();  // lowercase v

        // Build stations list
        List<Map<String, Object>> stationsList = fireStationRepository.findAll().stream()  // lowercase f
                .map(station -> {
                    long firefighterCount = firefighterRepository.findAll().stream()  // lowercase f
                            .filter(ff -> ff.getStation() != null &&
                                    ff.getStation().getId().equals(station.getId()))
                            .count();

                    long vehicleCount = vehicleRepository.findAll().stream()  // lowercase v
                            .filter(v -> v.getStation() != null &&
                                    v.getStation().getId().equals(station.getId()))
                            .count();

                    Map<String, Object> stationMap = new HashMap<>();
                    stationMap.put("id", station.getId());
                    stationMap.put("name", station.getName());
                    stationMap.put("firefighterCount", firefighterCount);
                    stationMap.put("vehicleCount", vehicleCount);
                    return stationMap;
                })
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("totalStations", totalStations);
        response.put("totalFirefighters", totalFirefighters);
        response.put("totalVehicles", totalVehicles);
        response.put("stations", stationsList);

        return ResponseEntity.ok(response);
    }

}