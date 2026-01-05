package org.resq.firepulseapi.registryservice.services;

import org.resq.firepulseapi.registryservice.dtos.FirefighterDto;
import org.resq.firepulseapi.registryservice.dtos.FirefighterFilters;
import org.resq.firepulseapi.registryservice.entities.Firefighter; // <-- IMPORT MANQUANT
import org.resq.firepulseapi.registryservice.exceptions.ApiException;
import org.resq.firepulseapi.registryservice.repositories.FirefighterRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FirefighterService {
    private final FirefighterRepository firefighterRepository;

    public FirefighterService(FirefighterRepository firefighterRepository) {
        this.firefighterRepository = firefighterRepository;
    }

    public FirefighterDto getFirefighterById(String firefighterId) {
        return firefighterRepository.findById(firefighterId)
                .map(FirefighterDto::fromEntity)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Firefighter not found"));
    }

    public FirefighterDto getFirefighterByUserId(String userId) {
        return firefighterRepository.findFirefighterByUserId(userId)
                .map(FirefighterDto::fromEntity)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Firefighter not found for the given user ID"));
    }

    public List<FirefighterDto> getAllFirefighters(FirefighterFilters filters) {
        // Récupère d'abord tous les pompiers avec leurs stations
        List<Firefighter> allFirefighters = firefighterRepository.findAll();

        return allFirefighters.stream()
                .map(firefighter -> {
                    // Crée le DTO
                    FirefighterDto dto = FirefighterDto.fromEntity(firefighter);

                    // Assure-toi que stationId n'est pas null
                    if (dto.getStationId() == null && firefighter.getStation() != null) {
                        dto.setStationId(firefighter.getStation().getId());
                    }

                    return dto;
                })
                .filter(firefighterDto -> { // CHANGÉ: firefighter → firefighterDto
                    // Filtre par stationId
                    if (filters.getStationId() != null && !filters.getStationId().isEmpty()) {
                        // Si le pompier n'a pas de station, on le filtre
                        if (firefighterDto.getStationId() == null) {
                            return false;
                        }
                        // Compare les IDs
                        return firefighterDto.getStationId().equals(filters.getStationId());
                    }

                    // Filtre par rank
                    if (filters.getRank() != null) {
                        return firefighterDto.getRank().equals(filters.getRank());
                    }

                    return true;
                })
                .toList();
    }
}