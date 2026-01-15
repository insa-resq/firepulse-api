package org.resq.firepulseapi.registryservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.resq.firepulseapi.registryservice.dtos.FireStationDto;
import org.resq.firepulseapi.registryservice.dtos.FireStationOverviewDto;
import org.resq.firepulseapi.registryservice.dtos.FireStationsFilters;
import org.resq.firepulseapi.registryservice.services.FireStationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fire-stations")
@Tag(name = "Fire Station Controller", description = "Endpoints for fire stations management")
public class FireStationController {
    private final FireStationService fireStationService;

    public FireStationController(FireStationService fireStationService) {
        this.fireStationService = fireStationService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get a list of all fire stations (admin only)")
    public ResponseEntity<List<FireStationDto>> getAllFireStations(@Valid @ModelAttribute FireStationsFilters filters) {
        List<FireStationDto> fireStations = fireStationService.getAllFireStations(filters);
        return ResponseEntity.ok(fireStations);
    }

    @GetMapping("/{stationId}")
    @Operation(summary = "Get a fire station by its ID")
    public ResponseEntity<FireStationDto> getFireStationById(@PathVariable String stationId) {
        FireStationDto fireStation = fireStationService.getFireStationById(stationId);
        return ResponseEntity.ok(fireStation);
    }

    @GetMapping("/{stationId}/overview")
    @Operation(summary = "Get a fire station by its ID")
    public ResponseEntity<FireStationOverviewDto> getFireStationOverview(@PathVariable String stationId) {
        FireStationOverviewDto overview = fireStationService.getFireStationOverview(stationId);
        return ResponseEntity.ok(overview);
    }

    @DeleteMapping("/{stationId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a fire station by its ID")
    public ResponseEntity<Void> deleteFireStation(@PathVariable String stationId) {
        fireStationService.deleteFireStationById(stationId);
        return ResponseEntity.noContent().build();
    }
}
