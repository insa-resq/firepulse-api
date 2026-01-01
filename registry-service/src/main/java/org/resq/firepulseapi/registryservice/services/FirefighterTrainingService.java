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
                    // Filtre par firefighterId
                    if (filters.getFirefighterId() != null && !filters.getFirefighterId().isEmpty()) {
                        if (!training.getFirefighterId().equals(filters.getFirefighterId())) {
                            return false;
                        }
                    }

                    // Filtre par type de permis
                    if (filters.getPermitType() != null && !filters.getPermitType().isEmpty()) {
                        Boolean hasPermit = switch (filters.getPermitType().toLowerCase()) {
                            case "permitb" -> training.getPermitB();
                            case "permitc" -> training.getPermitC();
                            case "permitaircraft" -> training.getPermitAircraft();
                            case "suap" -> training.getSuap();
                            case "inc" -> training.getInc();
                            case "smallteamleader" -> training.getSmallTeamLeader();
                            case "mediumteamleader" -> training.getMediumTeamLeader();
                            case "largeteamleader" -> training.getLargeTeamLeader();
                            default -> null;
                        };

                        if (hasPermit == null) return false;

                        // Si hasPermit est spécifié, vérifier la valeur
                        if (filters.getHasPermit() != null) {
                            if (hasPermit != filters.getHasPermit()) {
                                return false;
                            }
                        }
                    }

                    return true;
                })
                .toList();
    }

    public FirefighterTrainingDto getTrainingById(String trainingId) {
        return trainingRepository.findById(trainingId)
                .map(FirefighterTrainingDto::fromEntity)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Training not found"));
    }
}