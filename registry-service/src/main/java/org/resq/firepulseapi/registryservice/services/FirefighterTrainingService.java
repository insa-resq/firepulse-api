package org.resq.firepulseapi.registryservice.services;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.resq.firepulseapi.registryservice.dtos.FirefighterTrainingDto;
import org.resq.firepulseapi.registryservice.dtos.FirefighterTrainingFilters;
import org.resq.firepulseapi.registryservice.entities.FirefighterTraining;
import org.resq.firepulseapi.registryservice.exceptions.ApiException;
import org.resq.firepulseapi.registryservice.repositories.FirefighterTrainingRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FirefighterTrainingService {
    private final FirefighterTrainingRepository trainingRepository;

    public List<FirefighterTrainingDto> getAllTrainings(FirefighterTrainingFilters filters) {
        Specification<FirefighterTraining> specification = buildSpecificationFromFilters(filters);
        return trainingRepository.findAll(specification)
                .stream()
                .map(FirefighterTrainingDto::fromEntity)
                .toList();
    }

    public FirefighterTrainingDto getTrainingById(String trainingId) {
        return trainingRepository.findById(trainingId)
                .map(FirefighterTrainingDto::fromEntity)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Training not found"));
    }

    private Specification<FirefighterTraining> buildSpecificationFromFilters(FirefighterTrainingFilters filters) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filters.getFirefighterId() != null && !filters.getFirefighterId().isEmpty()) {
                predicates.add(criteriaBuilder.equal(
                        root.get("firefighter").get("id"),
                        filters.getFirefighterId()
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}