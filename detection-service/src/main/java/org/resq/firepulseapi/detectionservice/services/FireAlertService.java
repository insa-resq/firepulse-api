package org.resq.firepulseapi.detectionservice.services;

import jakarta.persistence.criteria.Predicate;
import org.resq.firepulseapi.detectionservice.dtos.*;
import org.resq.firepulseapi.detectionservice.entities.FireAlert;
import org.resq.firepulseapi.detectionservice.entities.Image;
import org.resq.firepulseapi.detectionservice.entities.enums.AlertStatus;
import org.resq.firepulseapi.detectionservice.entities.enums.UserRole;
import org.resq.firepulseapi.detectionservice.exceptions.ApiException;
import org.resq.firepulseapi.detectionservice.repositories.FireAlertRepository;
import org.resq.firepulseapi.detectionservice.repositories.ImageRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FireAlertService {
    private final FireAlertRepository fireAlertRepository;
    private final ImageRepository imageRepository;

    public FireAlertService(FireAlertRepository fireAlertRepository, ImageRepository imageRepository) {
        this.fireAlertRepository = fireAlertRepository;
        this.imageRepository = imageRepository;
    }

    public List<FireAlertDto> getFireAlerts(FireAlertsFilters filters) {
        Specification<FireAlert> spec = buildSpecificationFromFilters(filters);
        return fireAlertRepository.findAll(spec).stream()
                .map(FireAlertDto::fromEntity)
                .toList();
    }

    public FireAlertDto getFireAlertById(Integer fireAlertId) {
        return fireAlertRepository.findById(fireAlertId)
                .map(FireAlertDto::fromEntity)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "FireAlert not found"));
    }

    public FireAlertDto updateFireAlertStatus(Integer fireAlertId, FireAlertStatusUpdateDto fireAlertStatusUpdateDto, UserRole userRole) {
        FireAlert fireAlert = fireAlertRepository.findById(fireAlertId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "FireAlert not found"));

        if (fireAlert.getStatus() == fireAlertStatusUpdateDto.getStatus()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, String.format("The alert is already in %s status", fireAlert.getStatus()));
        }

        if (userRole != UserRole.ADMIN) {
            if (fireAlert.getStatus() == AlertStatus.RESOLVED || fireAlert.getStatus() == AlertStatus.DISMISSED) {
                throw new ApiException(HttpStatus.BAD_REQUEST, String.format("Cannot update status of a %s alert", fireAlert.getStatus()));
            }

            if (
                    (fireAlert.getStatus() == AlertStatus.IN_PROGRESS && fireAlertStatusUpdateDto.getStatus() == AlertStatus.NEW)
                            || (fireAlert.getStatus() == AlertStatus.NEW && fireAlertStatusUpdateDto.getStatus() == AlertStatus.RESOLVED)
            ) {
                throw new ApiException(HttpStatus.BAD_REQUEST, String.format("Invalid status transition from %s to %s", fireAlert.getStatus(), fireAlertStatusUpdateDto.getStatus()));
            }
        }

        fireAlert.setStatus(fireAlertStatusUpdateDto.getStatus());

        FireAlert updatedFireAlert = fireAlertRepository.save(fireAlert);

        return FireAlertDto.fromEntity(updatedFireAlert);
    }

    public FireAlertDto createFireAlert(FireAlertCreationDto fireAlertCreationDto) {
        Image image = imageRepository.findById(fireAlertCreationDto.getImageId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Associated image not found"));

        FireAlert fireAlert = new FireAlert();
        fireAlert.setDescription(fireAlertCreationDto.getDescription());
        fireAlert.setConfidence(fireAlertCreationDto.getConfidence());
        fireAlert.setLatitude(fireAlertCreationDto.getLatitude());
        fireAlert.setLongitude(fireAlertCreationDto.getLongitude());
        fireAlert.setSeverity(fireAlertCreationDto.getSeverity());
        fireAlert.setStatus(AlertStatus.NEW);
        fireAlert.setImage(image);

        FireAlert createdFireAlert = fireAlertRepository.save(fireAlert);

        return FireAlertDto.fromEntity(createdFireAlert);
    }

    @Transactional
    public void deleteFireAlerts(FireAlertsBulkDeletionDto fireAlertsBulkDeletionDto) {
        Set<Integer> fireAlertIdsSet = new HashSet<>(fireAlertsBulkDeletionDto.getFireAlertIds());
        List<FireAlert> fireAlertsToDelete = fireAlertRepository.findAllById(fireAlertIdsSet);

        if (fireAlertsToDelete.size() != fireAlertsBulkDeletionDto.getFireAlertIds().size()) {
            Set<Integer> foundIds = fireAlertsToDelete.stream()
                    .map(FireAlert::getId)
                    .collect(Collectors.toSet());

            fireAlertIdsSet.removeAll(foundIds);

            throw new ApiException(
                    HttpStatus.NOT_FOUND,
                    String.format(
                            "FireAlerts with the following IDs were not found: %s",
                            fireAlertIdsSet.stream().map(String::valueOf).collect(Collectors.joining(", "))
                    )
            );
        }

        fireAlertRepository.deleteAllInBatch(fireAlertsToDelete);
    }

    private Specification<FireAlert> buildSpecificationFromFilters(FireAlertsFilters filters) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filters.getSeverities() != null && !filters.getSeverities().isEmpty()) {
                predicates.add(root.get("severity").in(filters.getSeverities()));
            }

            if (filters.getStatuses() != null && !filters.getStatuses().isEmpty()) {
                predicates.add(root.get("status").in(filters.getStatuses()));
            }

            if (filters.getImageId() != null && !filters.getImageId().isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("image").get("id"), filters.getImageId()));
            }

            if (filters.getCreatedAt() != null) {
                predicates.add(criteriaBuilder.equal(root.get("createdAt"), filters.getCreatedAt()));
            } else {
                if (filters.getCreatedAtFrom() != null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), filters.getCreatedAtFrom()));
                }

                if (filters.getCreatedAtTo() != null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), filters.getCreatedAtTo()));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
