package org.resq.firepulseapi.planningservice.services;

import jakarta.persistence.criteria.Predicate;
import org.resq.firepulseapi.planningservice.clients.AccountsClient;
import org.resq.firepulseapi.planningservice.clients.RegistryClient;
import org.resq.firepulseapi.planningservice.dtos.*;
import org.resq.firepulseapi.planningservice.entities.Planning;
import org.resq.firepulseapi.planningservice.entities.ShiftAssignment;
import org.resq.firepulseapi.planningservice.entities.enums.UserRole;
import org.resq.firepulseapi.planningservice.exceptions.ApiException;
import org.resq.firepulseapi.planningservice.repositories.PlanningRepository;
import org.resq.firepulseapi.planningservice.repositories.ShiftAssignmentRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ShiftAssignmentService {
    private final ShiftAssignmentRepository shiftAssignmentRepository;
    private final PlanningRepository planningRepository;
    private final AccountsClient accountsClient;
    private final RegistryClient registryClient;

    static class CacheKey {
        public static final String SHIFT_ASSIGNMENTS_LIST = "SHIFT_ASSIGNMENTS_LIST";
        public static final String DETAILED_SHIFT_ASSIGNMENTS_LIST = "DETAILED_SHIFT_ASSIGNMENTS_LIST";
    }

    public ShiftAssignmentService(
            ShiftAssignmentRepository shiftAssignmentRepository,
            PlanningRepository planningRepository,
            AccountsClient accountsClient,
            RegistryClient registryClient
    ) {
        this.shiftAssignmentRepository = shiftAssignmentRepository;
        this.planningRepository = planningRepository;
        this.accountsClient = accountsClient;
        this.registryClient = registryClient;
    }

    @Cacheable(value = CacheKey.SHIFT_ASSIGNMENTS_LIST, key = "#userId + '-' + #userRole + '-' + #filters")
    public List<ShiftAssignmentDto> getShiftAssignments(String userId, UserRole userRole, ShiftAssignmentsFilters filters) {
        if (userRole == UserRole.FIREFIGHTER) {
            filters.setFirefighterId(userId);
        }

        if (userRole == UserRole.FIREFIGHTER || userRole == UserRole.PLANNING_MANAGER) {
            Planning planning = planningRepository.findById(filters.getPlanningId())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Planning not found"));

            UserDto user = accountsClient.getUserById(userId);

            if (!planning.getStationId().equals(user.getStationId())) {
                throw new ApiException(HttpStatus.FORBIDDEN, "You are not authorized to access this planning's shift assignments");
            }
        }

        Specification<ShiftAssignment> specification = buildSpecificationFromFilters(filters);

        return shiftAssignmentRepository.findAll(specification)
                .stream()
                .map(ShiftAssignmentDto::fromEntity)
                .toList();
    }

    @Cacheable(value = CacheKey.DETAILED_SHIFT_ASSIGNMENTS_LIST, key = "#userId + '-' + #userRole + '-' + #filters")
    public List<DetailedShiftAssignmentDto> getDetailedShiftAssignments(String userId, UserRole userRole, ShiftAssignmentsFilters filters) {
        Planning planning = planningRepository.findById(filters.getPlanningId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Planning not found"));

        if (userRole == UserRole.PLANNING_MANAGER) {
            UserDto user = accountsClient.getUserById(userId);

            if (!planning.getStationId().equals(user.getStationId())) {
                throw new ApiException(HttpStatus.FORBIDDEN, "You are not authorized to access this planning's shift assignments");
            }
        }

        Specification<ShiftAssignment> specification = buildSpecificationFromFilters(filters);

        Map<String, List<ShiftAssignment>> assignmentsByFirefighter = shiftAssignmentRepository.findAll(specification)
                .stream()
                .collect(Collectors.groupingBy(ShiftAssignment::getFirefighterId));

        Map<String, FirefighterDto> firefighterMap = registryClient.getFirefighters(planning.getStationId())
                .stream()
                .collect(Collectors.toMap(FirefighterDto::getId, firefighter -> firefighter));

        return assignmentsByFirefighter.values().stream()
                .flatMap(assignments ->
                        assignments.stream()
                        .map(shiftAssignment -> {
                            DetailedShiftAssignmentDto detailedShiftAssignment = new DetailedShiftAssignmentDto();
                            detailedShiftAssignment.setId(shiftAssignment.getId());
                            detailedShiftAssignment.setCreatedAt(shiftAssignment.getCreatedAt());
                            detailedShiftAssignment.setUpdatedAt(shiftAssignment.getUpdatedAt());
                            detailedShiftAssignment.setWeekday(shiftAssignment.getWeekday());
                            detailedShiftAssignment.setShiftType(shiftAssignment.getShiftType());
                            detailedShiftAssignment.setPlanningId(shiftAssignment.getPlanning().getId());
                            detailedShiftAssignment.setFirefighter(firefighterMap.get(shiftAssignment.getFirefighterId()));
                            return detailedShiftAssignment;
                        })
                )
                .toList();
    }

    private Specification<ShiftAssignment> buildSpecificationFromFilters(ShiftAssignmentsFilters filters) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filters.getPlanningId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("planning").get("id"), filters.getPlanningId()));
            }

            if (filters.getFirefighterId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("firefighterId"), filters.getFirefighterId()));
            }

            if (filters.getWeekday() != null) {
                predicates.add(criteriaBuilder.equal(root.get("weekday"), filters.getWeekday()));
            }

            if (filters.getShiftType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("shiftType"), filters.getShiftType()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
