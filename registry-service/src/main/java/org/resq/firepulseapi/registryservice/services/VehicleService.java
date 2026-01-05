package org.resq.firepulseapi.registryservice.services;

import org.resq.firepulseapi.registryservice.dtos.VehicleDto;
import org.resq.firepulseapi.registryservice.dtos.VehicleFilters;
import org.resq.firepulseapi.registryservice.exceptions.ApiException;
import org.resq.firepulseapi.registryservice.repositories.VehicleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleService {
    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public List<VehicleDto> getAllVehicles(VehicleFilters filters) {
        return vehicleRepository.findAll()
                .stream()
                .map(VehicleDto::fromEntity)
                .filter(vehicle -> {
                    // Filtre par stationId
                    if (filters.getStationId() != null && !filters.getStationId().isEmpty()) {
                        if (vehicle.getStationId() == null ||
                                !vehicle.getStationId().equals(filters.getStationId())) {
                            return false;
                        }
                    }

                    // Filtre par type
                    if (filters.getType() != null) {
                        if (!vehicle.getType().equals(filters.getType())) {
                            return false;
                        }
                    }

                    return true;
                })
                .toList();
    }

    public VehicleDto getVehicleById(String vehicleId) {
        return vehicleRepository.findById(vehicleId)
                .map(VehicleDto::fromEntity)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Vehicle not found"));
    }
}