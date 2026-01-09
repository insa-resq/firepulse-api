package org.resq.firepulseapi.planningservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.resq.firepulseapi.planningservice.annotations.AuthenticatedUserRole;
import org.resq.firepulseapi.planningservice.dtos.*;
import org.resq.firepulseapi.planningservice.entities.enums.UserRole;
import org.resq.firepulseapi.planningservice.services.PlanningService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/plannings")
@Tag(name = "Planning Controller", description = "Endpoints for managing plannings")
public class PlanningController {
    private final PlanningService planningService;

    public PlanningController(PlanningService planningService) {
        this.planningService = planningService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLANNING_MANAGER') or hasRole('FIREFIGHTER')")
    @Operation(summary = "Get plannings list with optional filters")
    public ResponseEntity<List<PlanningDto>> getPlannings(
            @AuthenticationPrincipal Jwt jwt,
            @AuthenticatedUserRole UserRole userRole,
            @Valid @ModelAttribute PlanningsFilters filters
    ) {
        String userId = jwt.getSubject();
        List<PlanningDto> plannings = planningService.getPlannings(userId, userRole, filters);
        return ResponseEntity.ok(plannings);
    }

    @GetMapping("/{planningId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLANNING_MANAGER') or hasRole('FIREFIGHTER')")
    @Operation(summary = "Get a planning by its ID")
    public ResponseEntity<PlanningDto> getPlanningById(
            @AuthenticationPrincipal Jwt jwt,
            @AuthenticatedUserRole UserRole userRole,
            @PathVariable String planningId
    ) {
        String userId = jwt.getSubject();
        PlanningDto planning = planningService.getPlanning(userId, userRole, planningId);
        return ResponseEntity.ok(planning);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLANNING_MANAGER')")
    @Operation(summary = "Create a new planning")
    public ResponseEntity<PlanningDto> createPlanning(
            @AuthenticationPrincipal Jwt jwt,
            @AuthenticatedUserRole UserRole userRole,
            @Valid @RequestBody PlanningCreationDto planningCreationDto
    ) {
        String userId = jwt.getSubject();
        PlanningDto planning = planningService.createPlanning(userId, userRole, planningCreationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(planning);
    }

    @PatchMapping("/{planningId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a planning by its ID (admin only)")
    public ResponseEntity<PlanningDto> updatePlanningById(
            @PathVariable String planningId,
            @Valid @RequestBody PlanningUpdateDto planningUpdateDto
    ) {
        PlanningDto planning = planningService.updatePlanning(planningId, planningUpdateDto);
        return ResponseEntity.ok(planning);
    }

    @PostMapping("/{planningId}/finalize")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Finalize a planning by its ID")
    public ResponseEntity<FinalizedPlanningDto> finalizePlanningById(
            @PathVariable String planningId,
            @Valid @RequestBody PlanningFinalizationDto planningFinalizationDto
    ) {
        FinalizedPlanningDto finalizedPlanning = planningService.finalizePlanning(planningId, planningFinalizationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(finalizedPlanning);
    }

    @PostMapping("/{planningId}/regenerate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLANNING_MANAGER')")
    @Operation(summary = "Regenerate a planning by its ID")
    public ResponseEntity<PlanningDto> regeneratePlanningById(
            @AuthenticationPrincipal Jwt jwt,
            @AuthenticatedUserRole UserRole userRole,
            @PathVariable String planningId
    ) {
        String userId = jwt.getSubject();
        PlanningDto planning = planningService.regeneratePlanning(userId, userRole, planningId);
        return ResponseEntity.ok(planning);
    }

    @DeleteMapping("/{planningId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLANNING_MANAGER')")
    @Operation(summary = "Delete a planning by its ID, along with all its shift assignments")
    public ResponseEntity<Void> deletePlanningById(
            @AuthenticationPrincipal Jwt jwt,
            @AuthenticatedUserRole UserRole userRole,
            @PathVariable String planningId
    ) {
        String userId = jwt.getSubject();
        planningService.deletePlanning(userId, userRole, planningId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{planningId}/shift-assignments")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLANNING_MANAGER')")
    @Operation(summary = "Delete all shift assignments associated with a planning by its ID")
    public ResponseEntity<Void> deleteShiftAssignmentsByPlanningId(
            @AuthenticationPrincipal Jwt jwt,
            @AuthenticatedUserRole UserRole userRole,
            @PathVariable String planningId
    ) {
        String userId = jwt.getSubject();
        planningService.deleteShiftAssignmentsByPlanningId(userId, userRole, planningId);
        return ResponseEntity.noContent().build();
    }
}
