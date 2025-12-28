package org.resq.firepulseapi.registryservice.services;

import org.resq.firepulseapi.registryservice.dtos.VehicleDto;
import org.resq.firepulseapi.registryservice.repositories.VehicleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleService {
    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public List<VehicleDto> getAllVehicles() {
        return vehicleRepository.findAll()
                .stream()
                .map(VehicleDto::fromEntity)
                .toList();
    }
}
