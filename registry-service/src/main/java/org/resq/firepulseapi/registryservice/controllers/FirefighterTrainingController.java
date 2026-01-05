package org.resq.firepulseapi.registryservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.resq.firepulseapi.registryservice.dtos.FirefighterTrainingDto;
import org.resq.firepulseapi.registryservice.dtos.FirefighterTrainingFilters;
import org.resq.firepulseapi.registryservice.services.FirefighterTrainingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/firefighter-trainings")
@Tag(name = "Firefighter Training Controller", description = "Endpoints for firefighter training management")
@RequiredArgsConstructor
public class FirefighterTrainingController {
    private final FirefighterTrainingService trainingService;

    @GetMapping
    @Operation(summary = "Get a list of all firefighter trainings")
    public ResponseEntity<List<FirefighterTrainingDto>> getAllTrainings(
            @Valid @ModelAttribute FirefighterTrainingFilters filters) {
        List<FirefighterTrainingDto> trainings = trainingService.getAllTrainings(filters);
        return ResponseEntity.ok(trainings);
    }

    @GetMapping("/{trainingId}")
    @Operation(summary = "Get a training by its ID")
    public ResponseEntity<FirefighterTrainingDto> getTrainingById(@PathVariable String trainingId) {
        FirefighterTrainingDto training = trainingService.getTrainingById(trainingId);
        return ResponseEntity.ok(training);
    }
}