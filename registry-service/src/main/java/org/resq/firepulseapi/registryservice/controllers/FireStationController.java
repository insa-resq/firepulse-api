package org.resq.firepulseapi.registryservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.resq.firepulseapi.registryservice.dtos.FireStationDto;
import org.resq.firepulseapi.registryservice.services.FireStationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fire-stations")
@Tag(name = "Fire Station Controller", description = "Endpoints for fire stations management")
public class FireStationController {
    private final FireStationService fireStationService;

    public FireStationController(FireStationService fireStationService) {
        this.fireStationService = fireStationService;
    }

    @GetMapping("/{stationId}")
    @Operation(summary = "Get a fire station by its ID")
    public ResponseEntity<FireStationDto> getFireStationById(@PathVariable String stationId) {
        FireStationDto fireStationDto = fireStationService.getFireStationById(stationId);
        return ResponseEntity.ok(fireStationDto);
    }
}
