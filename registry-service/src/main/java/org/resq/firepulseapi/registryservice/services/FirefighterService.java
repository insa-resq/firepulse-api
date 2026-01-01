package org.resq.firepulseapi.registryservice.services;

import org.resq.firepulseapi.registryservice.dtos.FirefighterDto;
import org.resq.firepulseapi.registryservice.dtos.FirefighterFilters;
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
        return firefighterRepository.findAll()
                .stream()
                .map(FirefighterDto::fromEntity)
                .filter(firefighter -> {
                    // Filtre par stationId
                    if (filters.getStationId() != null && !filters.getStationId().isEmpty()) {
                        if (firefighter.getStationId() == null ||
                                !firefighter.getStationId().equals(filters.getStationId())) {
                            return false;
                        }
                    }

                    // Filtre par rank
                    if (filters.getRank() != null && !filters.getRank().isEmpty()) {
                        if (!firefighter.getRank().toString().equals(filters.getRank())) {
                            return false;
                        }
                    }

                    return true;
                })
                .toList();
    }
}