package org.resq.firepulseapi.registryservice.services;

import org.resq.firepulseapi.registryservice.dtos.FireStationDto;
import org.resq.firepulseapi.registryservice.exceptions.ApiException;
import org.resq.firepulseapi.registryservice.repositories.FireStationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class FireStationService {
    private final FireStationRepository fireStationRepository;

    public FireStationService(FireStationRepository fireStationRepository) {
        this.fireStationRepository = fireStationRepository;
    }

    public FireStationDto getFireStationById(String stationId) {
        return fireStationRepository.findById(stationId)
                .map(FireStationDto::fromEntity)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Fire station not found"));
    }
}
