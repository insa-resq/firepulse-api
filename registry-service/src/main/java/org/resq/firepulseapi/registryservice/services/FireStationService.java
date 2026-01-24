package org.resq.firepulseapi.registryservice.services;

import jakarta.persistence.criteria.Predicate;
import org.resq.firepulseapi.registryservice.dtos.FireStationDto;
import org.resq.firepulseapi.registryservice.dtos.FireStationsFilters;
import org.resq.firepulseapi.registryservice.entities.FireStation;
import org.resq.firepulseapi.registryservice.exceptions.ApiException;
import org.resq.firepulseapi.registryservice.repositories.FireStationRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class FireStationService {
    private final FireStationRepository fireStationRepository;

    private static class CacheKey {
        public static final String FIRESTATION_BY_ID = "FIRESTATION_BY_ID";
        public static final String FIRESTATIONS_LIST = "FIRESTATIONS_LIST";
    }

    public FireStationService(FireStationRepository fireStationRepository) {
        this.fireStationRepository = fireStationRepository;
    }

    @Cacheable(value = CacheKey.FIRESTATIONS_LIST, key = "#filters")
    public List<FireStationDto> getAllFireStations(FireStationsFilters filters) {
        Specification<FireStation> specification = buildSpecificationFromFilters(filters);
        return fireStationRepository.findAll(specification)
                .stream()
                .map(FireStationDto::fromEntity)
                .toList();
    }

    @Cacheable(value = CacheKey.FIRESTATION_BY_ID, key = "#stationId")
    public FireStationDto getFireStationById(String stationId) {
        return fireStationRepository.findById(stationId)
                .map(FireStationDto::fromEntity)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Fire station not found"));
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheKey.FIRESTATIONS_LIST, allEntries = true),
            @CacheEvict(value = CacheKey.FIRESTATION_BY_ID, key = "#stationId")
    })
    public void deleteFireStationById(String stationId) {
        if (!fireStationRepository.existsById(stationId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Fire station not found");
        }
        fireStationRepository.deleteById(stationId);
    }

    private Specification<FireStation> buildSpecificationFromFilters(FireStationsFilters filters) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filters.getNameContains() != null && !filters.getNameContains().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + filters.getNameContains().toLowerCase() + "%"
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
