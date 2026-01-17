package org.resq.firepulseapi.registryservice.services;

import jakarta.persistence.criteria.Predicate;
import org.resq.firepulseapi.registryservice.dtos.VehicleDto;
import org.resq.firepulseapi.registryservice.dtos.VehicleFilters;
import org.resq.firepulseapi.registryservice.dtos.VehicleUpdateDto;
import org.resq.firepulseapi.registryservice.entities.Vehicle;
import org.resq.firepulseapi.registryservice.exceptions.ApiException;
import org.resq.firepulseapi.registryservice.repositories.VehicleRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class VehicleService {
    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public List<VehicleDto> getAllVehicles(VehicleFilters filters) {
        Specification<Vehicle> specification = buildSpecificationFromFilters(filters);
        return vehicleRepository.findAll(specification)
                .stream()
                .map(VehicleDto::fromEntity)
                .toList();
    }

    public VehicleDto getVehicleById(String vehicleId) {
        return vehicleRepository.findById(vehicleId)
                .map(VehicleDto::fromEntity)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Vehicle not found"));
    }

    public List<VehicleDto> updateVehicles(List<VehicleUpdateDto> vehicleUpdateDtos) {
        Map<String, Vehicle> vehiclesMap = vehicleRepository.findAllById(
                vehicleUpdateDtos.stream()
                        .map(VehicleUpdateDto::getVehicleId)
                        .toList()
                )
                .stream()
                .collect(Collectors.toMap(Vehicle::getId, vehicle -> vehicle));

        Set<String> foundVehiclesIds = vehiclesMap.keySet();

        List<String> notFoundVehiclesIds = vehicleUpdateDtos.stream()
                .map(VehicleUpdateDto::getVehicleId)
                .filter(id -> !foundVehiclesIds.contains(id))
                .toList();

        if (!notFoundVehiclesIds.isEmpty()) {
            throw new ApiException(
                    HttpStatus.NOT_FOUND,
                    "Vehicles not found: " + String.join(", ", notFoundVehiclesIds
            ));
        }

        List<Vehicle> updatedVehicles = vehicleUpdateDtos.stream()
                .filter(dto -> dto.getTotalCount() != null || dto.getAvailableCount() != null || dto.getBookedCount() != null)
                .map(dto -> {
                    Vehicle vehicle = vehiclesMap.get(dto.getVehicleId());
                    if (dto.getTotalCount() != null) {
                        vehicle.setTotalCount(dto.getTotalCount());
                    }
                    if (dto.getAvailableCount() != null) {
                        vehicle.setAvailableCount(dto.getAvailableCount());
                    }
                    if (dto.getBookedCount() != null) {
                        vehicle.setBookedCount(dto.getBookedCount());
                    }
                    return vehicle;
                })
                .toList();

        return vehicleRepository.saveAll(updatedVehicles)
                .stream()
                .map(VehicleDto::fromEntity)
                .toList();
    }

    private Specification<Vehicle> buildSpecificationFromFilters(VehicleFilters filters) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filters.getType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), filters.getType()));
            }

            if (filters.getStationId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("station").get("id"), filters.getStationId()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
