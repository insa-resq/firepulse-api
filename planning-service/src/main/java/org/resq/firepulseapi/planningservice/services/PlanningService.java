package org.resq.firepulseapi.planningservice.services;

import jakarta.persistence.criteria.Predicate;
import org.resq.firepulseapi.planningservice.clients.AccountsClient;
import org.resq.firepulseapi.planningservice.dtos.PlanningDto;
import org.resq.firepulseapi.planningservice.dtos.PlanningsFilters;
import org.resq.firepulseapi.planningservice.dtos.UserDto;
import org.resq.firepulseapi.planningservice.entities.Planning;
import org.resq.firepulseapi.planningservice.entities.enums.UserRole;
import org.resq.firepulseapi.planningservice.repositories.PlanningRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PlanningService {
    private final PlanningRepository planningRepository;
    private final AccountsClient accountsClient;

    public PlanningService(PlanningRepository planningRepository, AccountsClient accountsClient) {
        this.planningRepository = planningRepository;
        this.accountsClient = accountsClient;
    }

    public List<PlanningDto> getPlannings(String userId, UserRole userRole, PlanningsFilters filters) {
        if (userRole == UserRole.FIREFIGHTER || userRole == UserRole.PLANNING_MANAGER) {
            UserDto user = accountsClient.getUserById(userId);
            filters.setStationId(user.getStationId());
        }

        Specification<Planning> specification = buildSpecificationFromFilters(filters);

        return planningRepository.findAll(specification)
                .stream()
                .map(PlanningDto::fromEntity)
                .toList();
    }

    private Specification<Planning> buildSpecificationFromFilters(PlanningsFilters filters) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filters.getYear() != null) {
                predicates.add(criteriaBuilder.equal(root.get("year"), filters.getYear()));
            }

            if (filters.getWeekNumber() != null) {
                predicates.add(criteriaBuilder.equal(root.get("weekNumber"), filters.getWeekNumber()));
            }

            if (filters.getStationId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("stationId"), filters.getStationId()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
