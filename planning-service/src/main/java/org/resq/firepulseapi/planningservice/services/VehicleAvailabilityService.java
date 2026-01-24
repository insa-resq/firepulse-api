package org.resq.firepulseapi.planningservice.services;

import jakarta.persistence.criteria.Predicate;
import org.resq.firepulseapi.planningservice.dtos.*;
import org.resq.firepulseapi.planningservice.entities.VehicleAvailability;
import org.resq.firepulseapi.planningservice.exceptions.ApiException;
import org.resq.firepulseapi.planningservice.repositories.VehicleAvailabilityRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VehicleAvailabilityService {
    private final VehicleAvailabilityRepository vehicleAvailabilityRepository;

    private static class CacheKey {
        public static final String VEHICLE_AVAILABILITIES_LIST = "VEHICLE_AVAILABILITIES_LIST";
    }

    public VehicleAvailabilityService(VehicleAvailabilityRepository vehicleAvailabilityRepository) {
        this.vehicleAvailabilityRepository = vehicleAvailabilityRepository;
    }

    @Cacheable(value = CacheKey.VEHICLE_AVAILABILITIES_LIST, key = "#filters")
    public List<VehicleAvailabilityDto> getVehicleAvailabilities(VehicleAvailabilitiesFilters filters) {
        Specification<VehicleAvailability> specification = buildSpecificationFromFilters(filters);
        return vehicleAvailabilityRepository.findAll(specification)
                .stream()
                .map(VehicleAvailabilityDto::fromEntity)
                .toList();
    }

    @Transactional
    @CacheEvict(value = CacheKey.VEHICLE_AVAILABILITIES_LIST, allEntries = true)
    public List<VehicleAvailabilityDto> updateVehicleAvailabilities(List<VehicleAvailabilityUpdateDto> vehicleAvailabilityUpdateDtos) {
        List<String> vehicleAvailabilityIds = vehicleAvailabilityUpdateDtos.stream()
                .map(VehicleAvailabilityUpdateDto::getAvailabilityId)
                .toList();

        Map<String, VehicleAvailability> vehicleAvailabilitiesMap = vehicleAvailabilityRepository.findAllById(vehicleAvailabilityIds)
                .stream()
                .collect(Collectors.toMap(VehicleAvailability::getId, va -> va));

        if (vehicleAvailabilitiesMap.size() != vehicleAvailabilityIds.size()) {
            List<String> notFoundIds = vehicleAvailabilityIds.stream()
                    .filter(id -> !vehicleAvailabilitiesMap.containsKey(id))
                    .toList();

            throw new ApiException(HttpStatus.NOT_FOUND, "Vehicle availabilities not found: " + String.join(", ", notFoundIds));
        }

        List<VehicleAvailability> vehicleAvailabilitiesToUpdate = vehicleAvailabilityUpdateDtos.stream()
                .map(dto -> {
                    VehicleAvailability va = vehicleAvailabilitiesMap.get(dto.getAvailabilityId());
                    if (dto.getBookedCount() != null) {
                        va.setBookedCount(dto.getBookedCount());
                    }
                    return va;
                })
                .toList();

        return vehicleAvailabilityRepository.saveAll(vehicleAvailabilitiesToUpdate)
                .stream()
                .map(VehicleAvailabilityDto::fromEntity)
                .toList();
    }

    private Specification<VehicleAvailability> buildSpecificationFromFilters(VehicleAvailabilitiesFilters filters) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filters.getVehicleIds() != null && !filters.getVehicleIds().isEmpty()) {
                predicates.add(root.get("vehicleId").in(filters.getVehicleIds()));
            }

            if (filters.getWeekday() != null) {
                predicates.add(criteriaBuilder.equal(root.get("weekday"), filters.getWeekday()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
