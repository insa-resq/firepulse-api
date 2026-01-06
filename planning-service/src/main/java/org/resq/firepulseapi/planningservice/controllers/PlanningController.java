package org.resq.firepulseapi.planningservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.resq.firepulseapi.planningservice.annotations.AuthenticatedUserRole;
import org.resq.firepulseapi.planningservice.dtos.PlanningDto;
import org.resq.firepulseapi.planningservice.dtos.PlanningsFilters;
import org.resq.firepulseapi.planningservice.entities.enums.UserRole;
import org.resq.firepulseapi.planningservice.services.PlanningService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
