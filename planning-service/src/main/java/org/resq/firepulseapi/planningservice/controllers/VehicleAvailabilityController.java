package org.resq.firepulseapi.planningservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.resq.firepulseapi.planningservice.dtos.*;
import org.resq.firepulseapi.planningservice.services.VehicleAvailabilityService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicle-availabilities")
@Tag(name = "Vehicle Availability Controller", description = "Endpoints for managing vehicle availabilities")
public class VehicleAvailabilityController {
    private final VehicleAvailabilityService vehicleAvailabilityService;

    public VehicleAvailabilityController(VehicleAvailabilityService vehicleAvailabilityService) {
        this.vehicleAvailabilityService = vehicleAvailabilityService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLANNING_MANAGER')")
    @Operation(summary = "Get vehicle availabilities list with optional filters")
    public ResponseEntity<List<VehicleAvailabilityDto>> getAvailabilitySlots(
            @Valid @ModelAttribute VehicleAvailabilitiesFilters filters
    ) {
        List<VehicleAvailabilityDto> vehicleAvailabilities = vehicleAvailabilityService.getVehicleAvailabilities(filters);
        return ResponseEntity.ok(vehicleAvailabilities);
    }

    @PatchMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLANNING_MANAGER')")
    @Operation(summary = "Update a vehicle availability slot")
    public ResponseEntity<List<VehicleAvailabilityDto>> updateAvailabilitySlot(
            @Valid @RequestBody List<@Valid VehicleAvailabilityUpdateDto> vehicleAvailabilityUpdateDtos
    ) {
        List<VehicleAvailabilityDto> updatedAvailabilities = vehicleAvailabilityService.updateVehicleAvailabilities(vehicleAvailabilityUpdateDtos);
        return ResponseEntity.ok(updatedAvailabilities);
    }
}
