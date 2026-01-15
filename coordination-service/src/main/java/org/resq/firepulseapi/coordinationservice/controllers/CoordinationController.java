package org.resq.firepulseapi.coordinationservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.resq.firepulseapi.coordinationservice.dtos.FireStationOverviewDto;
import org.resq.firepulseapi.coordinationservice.services.CoordinationService;
import org.resq.firepulseapi.coordinationservice.dtos.FireStationDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/coordination")
@Tag(name = "Coordination Controller", description = "Endpoints for coordination team")
public class CoordinationController {
    private final CoordinationService coordinationService;

    public CoordinationController(CoordinationService coordinationService) {
        this.coordinationService = coordinationService;
    }

    @GetMapping("/fire-stations")
    @Operation(summary = "Get a list of all fire stations")
    public ResponseEntity<List<FireStationDto>> getFireStations() {
        List<FireStationDto> fireStations = coordinationService.getAllFireStations();
        return ResponseEntity.ok(fireStations);
    }

    @GetMapping("/fire-stations/{stationId}/overview")
    @Operation(summary = "Get an overview of a specific fire station")
    public ResponseEntity<FireStationOverviewDto> getFireStationOverview(@PathVariable String stationId) {
        FireStationOverviewDto overview = coordinationService.getFireStationOverview(stationId);
        return ResponseEntity.ok(overview);
    }
}
