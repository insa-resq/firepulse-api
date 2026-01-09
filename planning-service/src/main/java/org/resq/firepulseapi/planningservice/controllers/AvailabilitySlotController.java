package org.resq.firepulseapi.planningservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.resq.firepulseapi.planningservice.dtos.AvailabilitySlotCreationDto;
import org.resq.firepulseapi.planningservice.dtos.AvailabilitySlotDto;
import org.resq.firepulseapi.planningservice.dtos.AvailabilitySlotUpdateDto;
import org.resq.firepulseapi.planningservice.dtos.AvailabilitySlotsFilters;
import org.resq.firepulseapi.planningservice.services.AvailabilitySlotService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/availability-slots")
@Tag(name = "Availability Slot Controller", description = "Endpoints for managing availability slots")
public class AvailabilitySlotController {
    private final AvailabilitySlotService availabilitySlotService;

    public AvailabilitySlotController(AvailabilitySlotService availabilitySlotService) {
        this.availabilitySlotService = availabilitySlotService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLANNING_MANAGER')")
    @Operation(summary = "Get availability slots list with optional filters")
    public ResponseEntity<List<AvailabilitySlotDto>> getAvailabilitySlots(
            @Valid @ModelAttribute AvailabilitySlotsFilters filters
    ) {
        List<AvailabilitySlotDto> availabilitySlots = availabilitySlotService.getAvailabilitySlots(filters);
        return ResponseEntity.ok(availabilitySlots);
    }

    @PostMapping
    @PreAuthorize("hasRole('FIREFIGHTER')")
    @Operation(summary = "Create a new availability slot")
    public ResponseEntity<AvailabilitySlotDto> createAvailabilitySlot(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody AvailabilitySlotCreationDto availabilitySlotCreationDto
    ) {
        String userId = jwt.getSubject();
        AvailabilitySlotDto createdAvailabilitySlot = availabilitySlotService.createAvailabilitySlot(
                userId,
                availabilitySlotCreationDto
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAvailabilitySlot);
    }

    @GetMapping("/{availabilitySlotId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLANNING_MANAGER') or hasRole('FIREFIGHTER')")
    @Operation(summary = "Get an availability slot by ID")
    public ResponseEntity<AvailabilitySlotDto> getAvailabilitySlotById(
            @PathVariable String availabilitySlotId
    ) {
        AvailabilitySlotDto availabilitySlot = availabilitySlotService.getAvailabilitySlotById(availabilitySlotId);
        return ResponseEntity.ok(availabilitySlot);
    }

    @PatchMapping("/{availabilitySlotId}")
    @PreAuthorize("hasRole('FIREFIGHTER')")
    @Operation(summary = "Update an existing availability slot")
    public ResponseEntity<AvailabilitySlotDto> updateAvailabilitySlot(
            @PathVariable String availabilitySlotId,
            @Valid @RequestBody AvailabilitySlotUpdateDto availabilitySlotUpdateDto
    ) {
        AvailabilitySlotDto updatedAvailabilitySlot = availabilitySlotService.updateAvailabilitySlot(
                availabilitySlotId,
                availabilitySlotUpdateDto
        );
        return ResponseEntity.ok(updatedAvailabilitySlot);
    }
}
