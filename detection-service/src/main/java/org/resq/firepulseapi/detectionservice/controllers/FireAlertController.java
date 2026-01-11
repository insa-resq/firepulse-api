package org.resq.firepulseapi.detectionservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.resq.firepulseapi.detectionservice.annotations.AuthenticatedUserRole;
import org.resq.firepulseapi.detectionservice.dtos.*;
import org.resq.firepulseapi.detectionservice.entities.enums.UserRole;
import org.resq.firepulseapi.detectionservice.services.FireAlertService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fire-alerts")
@Tag(name = "Fire Alert Controller", description = "Endpoints for managing fire alerts")
public class FireAlertController {
    private final FireAlertService fireAlertService;

    public FireAlertController(FireAlertService fireAlertService) {
        this.fireAlertService = fireAlertService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('ALERT_MONITOR')")
    @Operation(summary = "Get fire alerts list with optional filters")
    public ResponseEntity<List<FireAlertDto>> getFireAlerts(@Valid @ModelAttribute FireAlertsFilters filters) {
        List<FireAlertDto> fireAlerts = fireAlertService.getFireAlerts(filters);
        return ResponseEntity.ok(fireAlerts);
    }

    @GetMapping("/{fireAlertId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ALERT_MONITOR')")
    @Operation(summary = "Get a fire alert by ID")
    public ResponseEntity<FireAlertDto> getFireAlertById(@PathVariable Integer fireAlertId) {
        FireAlertDto fireAlert = fireAlertService.getFireAlertById(fireAlertId);
        return ResponseEntity.ok(fireAlert);
    }

    @PatchMapping("/{fireAlertId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ALERT_MONITOR')")
    @Operation(summary = "Update a fire alert status")
    public ResponseEntity<FireAlertDto> updateFireAlertStatus(
            @AuthenticatedUserRole UserRole userRole,
            @PathVariable Integer fireAlertId,
            @Valid @RequestBody FireAlertStatusUpdateDto fireAlertStatusUpdateDto
    ) {
        FireAlertDto updatedFireAlert = fireAlertService.updateFireAlertStatus(fireAlertId, fireAlertStatusUpdateDto, userRole);
        return ResponseEntity.ok(updatedFireAlert);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a fire alert")
    public ResponseEntity<FireAlertDto> createFireAlert(@Valid @RequestBody FireAlertCreationDto fireAlertCreationDto) {
        FireAlertDto createdFireAlert = fireAlertService.createFireAlert(fireAlertCreationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFireAlert);
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete one or multiple fire alerts")
    public ResponseEntity<Void> deleteFireAlerts(@Valid @RequestBody FireAlertsBulkDeletionDto fireAlertsBulkDeletionDto) {
        fireAlertService.deleteFireAlerts(fireAlertsBulkDeletionDto);
        return ResponseEntity.noContent().build();
    }
}
