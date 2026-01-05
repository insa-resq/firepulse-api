package org.resq.firepulseapi.registryservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.resq.firepulseapi.registryservice.dtos.FirefighterDto;
import org.resq.firepulseapi.registryservice.dtos.FirefighterFilters;
import org.resq.firepulseapi.registryservice.services.FirefighterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/firefighters")
@Tag(name = "Firefighter Controller", description = "Endpoints for firefighter management")
public class FirefighterController {
    private final FirefighterService firefighterService;

    public FirefighterController(FirefighterService firefighterService) {
        this.firefighterService = firefighterService;
    }

    @GetMapping
    @Operation(summary = "Get a list of all firefighters")
    public ResponseEntity<List<FirefighterDto>> getAllFirefighters(@Valid @ModelAttribute FirefighterFilters filters) {
        List<FirefighterDto> firefighters = firefighterService.getAllFirefighters(filters);
        return ResponseEntity.ok(firefighters);
    }

    @GetMapping("/{firefighterId}")
    @Operation(summary = "Get a firefighter by its ID")
    public ResponseEntity<FirefighterDto> getFirefighterById(@PathVariable String firefighterId) {
        FirefighterDto firefighterDto = firefighterService.getFirefighterById(firefighterId);
        return ResponseEntity.ok(firefighterDto);
    }

    @GetMapping("/users/{userId}")
    @Operation(summary = "Get a firefighter by user ID")
    public ResponseEntity<FirefighterDto> getFirefighterByUserId(@PathVariable String userId) {
        FirefighterDto firefighterDto = firefighterService.getFirefighterByUserId(userId);
        return ResponseEntity.ok(firefighterDto);
    }

}