package org.resq.firepulseapi.registryservice.services;

import jakarta.persistence.criteria.Predicate;
import org.resq.firepulseapi.registryservice.dtos.VehicleDto;
import org.resq.firepulseapi.registryservice.dtos.VehicleFilters;
import org.resq.firepulseapi.registryservice.entities.Vehicle;
import org.resq.firepulseapi.registryservice.exceptions.ApiException;
import org.resq.firepulseapi.registryservice.repositories.VehicleRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VehicleService {
    private final VehicleRepository vehicleRepository;

    private static class CacheKey {
        public static final String VEHICLE_BY_ID = "VEHICLE_BY_ID";
        public static final String VEHICLES_LIST = "VEHICLES_LIST";
    }

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Cacheable(value = CacheKey.VEHICLES_LIST, key = "#filters")
    public List<VehicleDto> getAllVehicles(VehicleFilters filters) {
        Specification<Vehicle> specification = buildSpecificationFromFilters(filters);
        return vehicleRepository.findAll(specification)
                .stream()
                .map(VehicleDto::fromEntity)
                .toList();
    }

    @Cacheable(value = CacheKey.VEHICLE_BY_ID, key = "#vehicleId")
    public VehicleDto getVehicleById(String vehicleId) {
        return vehicleRepository.findById(vehicleId)
                .map(VehicleDto::fromEntity)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Vehicle not found"));
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
