package org.resq.firepulseapi.planningservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.resq.firepulseapi.planningservice.annotations.AuthenticatedUserRole;
import org.resq.firepulseapi.planningservice.dtos.*;
import org.resq.firepulseapi.planningservice.entities.enums.UserRole;
import org.resq.firepulseapi.planningservice.services.ShiftAssignmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shift-assignments")
@Tag(name = "Shift Assignment Controller", description = "Endpoints for managing shift assignments")
public class ShiftAssignmentController {
    private final ShiftAssignmentService shiftAssignmentService;

    public ShiftAssignmentController(ShiftAssignmentService shiftAssignmentService) {
        this.shiftAssignmentService = shiftAssignmentService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLANNING_MANAGER') or hasRole('FIREFIGHTER')")
    @Operation(summary = "Get shift assignments with optional filters")
    public ResponseEntity<List<ShiftAssignmentDto>> getPlannings(
            @AuthenticationPrincipal Jwt jwt,
            @AuthenticatedUserRole UserRole userRole,
            @Valid @ModelAttribute ShiftAssignmentsFilters filters
    ) {
        String userId = jwt.getSubject();
        List<ShiftAssignmentDto> shiftAssignments = shiftAssignmentService.getShiftAssignments(userId, userRole, filters);
        return ResponseEntity.ok(shiftAssignments);
    }

    @GetMapping("detailed")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLANNING_MANAGER')")
    @Operation(summary = "Get detailed shift assignments with optional filters")
    public ResponseEntity<List<DetailedShiftAssignmentDto>> getDetailedShiftAssignments(
            @AuthenticationPrincipal Jwt jwt,
            @AuthenticatedUserRole UserRole userRole,
            @Valid @ModelAttribute ShiftAssignmentsFilters filters
    ) {
        String userId = jwt.getSubject();
        List<DetailedShiftAssignmentDto> detailedShiftAssignments = shiftAssignmentService.getDetailedShiftAssignments(userId, userRole, filters);
        return ResponseEntity.ok(detailedShiftAssignments);
    }
}
