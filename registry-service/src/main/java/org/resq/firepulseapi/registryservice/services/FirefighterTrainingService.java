package org.resq.firepulseapi.registryservice.services;

import lombok.RequiredArgsConstructor;
import org.resq.firepulseapi.registryservice.dtos.FirefighterTrainingDto;
import org.resq.firepulseapi.registryservice.dtos.FirefighterTrainingFilters;
import org.resq.firepulseapi.registryservice.entities.FirefighterTraining;
import org.resq.firepulseapi.registryservice.exceptions.ApiException;
import org.resq.firepulseapi.registryservice.repositories.FirefighterTrainingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FirefighterTrainingService {
    private final FirefighterTrainingRepository trainingRepository;

    public List<FirefighterTrainingDto> getAllTrainings(FirefighterTrainingFilters filters) {
        return trainingRepository.findAll()
                .stream()
                .map(FirefighterTrainingDto::fromEntity)
                .filter(training -> {
                    // Filtre SEULEMENT par firefighterId
                    if (filters.getFirefighterId() != null && !filters.getFirefighterId().isEmpty()) {
                        return training.getFirefighterId().equals(filters.getFirefighterId());
                    }

                    return true; // Si pas de filtre, garde tout
                })
                .toList();
    }

    public FirefighterTrainingDto getTrainingById(String trainingId) {
        return trainingRepository.findById(trainingId)
                .map(FirefighterTrainingDto::fromEntity)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Training not found"));
    }
}