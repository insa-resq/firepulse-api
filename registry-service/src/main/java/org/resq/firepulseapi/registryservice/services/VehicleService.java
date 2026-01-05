package org.resq.firepulseapi.registryservice.services;

import jakarta.persistence.criteria.Predicate;
import org.resq.firepulseapi.registryservice.dtos.VehicleDto;
import org.resq.firepulseapi.registryservice.dtos.VehicleFilters;
import org.resq.firepulseapi.registryservice.entities.Vehicle;
import org.resq.firepulseapi.registryservice.entities.enums.VehicleType;
import org.resq.firepulseapi.registryservice.exceptions.ApiException;
import org.resq.firepulseapi.registryservice.repositories.VehicleRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    private Specification<Vehicle> buildSpecificationFromFilters(VehicleFilters filters) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filters.getStationId() != null && !filters.getStationId().isEmpty()) {
                predicates.add(criteriaBuilder.equal(
                        root.get("station").get("id"),
                        filters.getStationId()
                ));
            }

            if (filters.getType() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("type"),
                        filters.getType()
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}