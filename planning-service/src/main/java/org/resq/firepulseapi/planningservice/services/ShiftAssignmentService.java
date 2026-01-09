package org.resq.firepulseapi.planningservice.services;

import jakarta.persistence.criteria.Predicate;
import org.resq.firepulseapi.planningservice.clients.AccountsClient;
import org.resq.firepulseapi.planningservice.dtos.ShiftAssignmentDto;
import org.resq.firepulseapi.planningservice.dtos.ShiftAssignmentsFilters;
import org.resq.firepulseapi.planningservice.dtos.UserDto;
import org.resq.firepulseapi.planningservice.entities.Planning;
import org.resq.firepulseapi.planningservice.entities.ShiftAssignment;
import org.resq.firepulseapi.planningservice.entities.enums.UserRole;
import org.resq.firepulseapi.planningservice.exceptions.ApiException;
import org.resq.firepulseapi.planningservice.repositories.PlanningRepository;
import org.resq.firepulseapi.planningservice.repositories.ShiftAssignmentRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ShiftAssignmentService {
    private final ShiftAssignmentRepository shiftAssignmentRepository;
    private final PlanningRepository planningRepository;
    private final AccountsClient accountsClient;

    public ShiftAssignmentService(
            ShiftAssignmentRepository shiftAssignmentRepository,
            PlanningRepository planningRepository,
            AccountsClient accountsClient
    ) {
        this.shiftAssignmentRepository = shiftAssignmentRepository;
        this.planningRepository = planningRepository;
        this.accountsClient = accountsClient;
    }

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

    private Specification<ShiftAssignment> buildSpecificationFromFilters(ShiftAssignmentsFilters filters) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filters.getPlanningId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("planningId"), filters.getPlanningId()));
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
