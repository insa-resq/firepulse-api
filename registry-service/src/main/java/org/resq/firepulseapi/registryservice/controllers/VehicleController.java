package org.resq.firepulseapi.registryservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.resq.firepulseapi.registryservice.dtos.VehicleDto;
import org.resq.firepulseapi.registryservice.dtos.VehicleFilters;
import org.resq.firepulseapi.registryservice.services.VehicleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicles")
@Tag(name = "Vehicle Controller", description = "Endpoints for vehicle management")
public class VehicleController {
    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    @Operation(summary = "Get a list of all vehicles")
    public ResponseEntity<List<VehicleDto>> getAllVehicles(@Valid @ModelAttribute VehicleFilters filters) {
        List<VehicleDto> vehicles = vehicleService.getAllVehicles(filters);
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/{vehicleId}")
    @Operation(summary = "Get a vehicle by its ID")
    public ResponseEntity<VehicleDto> getVehicleById(@PathVariable String vehicleId) {
        VehicleDto vehicle = vehicleService.getVehicleById(vehicleId);
        return ResponseEntity.ok(vehicle);
    }

    @GetMapping("/by-station/{stationId}")
    @Operation(summary = "Get all vehicles for a specific station")
    public ResponseEntity<List<VehicleDto>> getVehiclesByStation(@PathVariable String stationId) {
        VehicleFilters filters = new VehicleFilters();
        filters.setStationId(stationId);
        List<VehicleDto> vehicles = vehicleService.getAllVehicles(filters);
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/by-station/{stationId}/type/{vehicleType}")
    @Operation(summary = "Get vehicles of a specific type for a station")
    public ResponseEntity<List<VehicleDto>> getVehiclesByStationAndType(
            @PathVariable String stationId,
            @PathVariable String vehicleType) {
        VehicleFilters filters = new VehicleFilters();
        filters.setStationId(stationId);

        try {
            filters.setType(org.resq.firepulseapi.registryservice.entities.enums.VehicleType.valueOf(vehicleType.toUpperCase()));
        } catch (IllegalArgumentException e) {
            // Retourne liste vide si type invalide
            return ResponseEntity.ok(List.of());
        }

        List<VehicleDto> vehicles = vehicleService.getAllVehicles(filters);
        return ResponseEntity.ok(vehicles);
    }

}