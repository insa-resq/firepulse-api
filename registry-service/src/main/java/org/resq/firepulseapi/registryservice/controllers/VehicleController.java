package org.resq.firepulseapi.registryservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.resq.firepulseapi.registryservice.dtos.VehicleDto;
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
    public ResponseEntity<List<VehicleDto>> getAllVehicles() {
        List<VehicleDto> vehicles = vehicleService.getAllVehicles();
        return ResponseEntity.ok(vehicles);
    }

    // AJOUTER CETTE MÉTHODE
    @GetMapping("/{vehicleId}")
    @Operation(summary = "Get a vehicle by its ID")
    public ResponseEntity<VehicleDto> getVehicleById(@PathVariable String vehicleId) {
        VehicleDto vehicle = vehicleService.getVehicleById(vehicleId);
        return ResponseEntity.ok(vehicle);
    }
}