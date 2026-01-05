package org.resq.firepulseapi.registryservice.services;

import jakarta.persistence.criteria.Predicate;
import org.resq.firepulseapi.registryservice.dtos.FirefighterDto;
import org.resq.firepulseapi.registryservice.dtos.FirefighterFilters;
import org.resq.firepulseapi.registryservice.entities.Firefighter;
import org.resq.firepulseapi.registryservice.exceptions.ApiException;
import org.resq.firepulseapi.registryservice.repositories.FirefighterRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FirefighterService {
    private final FirefighterRepository firefighterRepository;

    public FirefighterService(FirefighterRepository firefighterRepository) {
        this.firefighterRepository = firefighterRepository;
    }

    public List<FirefighterDto> getAllFirefighters(FirefighterFilters filters) {
        Specification<Firefighter> specification = buildSpecificationFromFilters(filters);
        return firefighterRepository.findAll(specification)
                .stream()
                .map(FirefighterDto::fromEntity)
                .toList();
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

    private Specification<Firefighter> buildSpecificationFromFilters(FirefighterFilters filters) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filters.getStationId() != null && !filters.getStationId().isEmpty()) {
                predicates.add(criteriaBuilder.equal(
                        root.get("station").get("id"),
                        filters.getStationId()
                ));
            }

            if (filters.getRank() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("rank"),
                        filters.getRank()
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}