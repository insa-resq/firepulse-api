package org.resq.firepulseapi.planningservice.services;

import feign.FeignException;
import jakarta.persistence.criteria.Predicate;
import org.resq.firepulseapi.planningservice.clients.AccountsClient;
import org.resq.firepulseapi.planningservice.clients.RegistryClient;
import org.resq.firepulseapi.planningservice.dtos.*;
import org.resq.firepulseapi.planningservice.entities.Planning;
import org.resq.firepulseapi.planningservice.entities.ShiftAssignment;
import org.resq.firepulseapi.planningservice.entities.enums.PlanningStatus;
import org.resq.firepulseapi.planningservice.entities.enums.UserRole;
import org.resq.firepulseapi.planningservice.exceptions.ApiException;
import org.resq.firepulseapi.planningservice.repositories.PlanningRepository;
import org.resq.firepulseapi.planningservice.repositories.ShiftAssignmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class PlanningService {
    private static final Logger logger = LoggerFactory.getLogger(PlanningService.class);
    private final PlanningRepository planningRepository;
    private final ShiftAssignmentRepository shiftAssignmentRepository;
    private final AccountsClient accountsClient;
    private final RegistryClient registryClient;
    private final RestTemplate restTemplate;

    @Value("${http.planning-engine-api.base-url}")
    private String planningEngineApiBaseUrl;

    public PlanningService(
            PlanningRepository planningRepository,
            ShiftAssignmentRepository shiftAssignmentRepository,
            AccountsClient accountsClient,
            RegistryClient registryClient,
            RestTemplate restTemplate
    ) {
        this.planningRepository = planningRepository;
        this.shiftAssignmentRepository = shiftAssignmentRepository;
        this.accountsClient = accountsClient;
        this.registryClient = registryClient;
        this.restTemplate = restTemplate;
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

    public PlanningDto getPlanning(String userId, UserRole userRole, String planningId) {
        Planning planning = planningRepository.findById(planningId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Planning not found"));

        if (userRole == UserRole.FIREFIGHTER || userRole == UserRole.PLANNING_MANAGER) {
            ensureUserIsFromStation(userId, planning.getStationId());
        }

        return PlanningDto.fromEntity(planning);
    }

    public PlanningDto createPlanning(String userId, UserRole userRole, PlanningCreationDto planningCreationDto) {
        try {
            registryClient.getFireStationById(planningCreationDto.getStationId());
        } catch (FeignException.NotFound e) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Fire station not found");
        }

        if (userRole == UserRole.PLANNING_MANAGER) {
            ensureUserIsFromStation(userId, planningCreationDto.getStationId());
        }

        Specification<Planning> specification = buildSpecificationFromFilters(
                new PlanningsFilters(
                        planningCreationDto.getYear(),
                        planningCreationDto.getWeekNumber(),
                        planningCreationDto.getStationId()
                )
        );

        if (planningRepository.exists(specification)) {
            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "Planning already exists for the specified year, week number and station. Try regenerating it instead."
            );
        }

        Planning planning = new Planning();
        planning.setYear(planningCreationDto.getYear());
        planning.setWeekNumber(planningCreationDto.getWeekNumber());
        planning.setStatus(PlanningStatus.GENERATING);
        planning.setStationId(planningCreationDto.getStationId());

        Planning newPlanning = planningRepository.save(planning);

        try {
            startPlanningGeneration(planning.getId());
        } catch (Exception e) {
            logger.error("Failed to start planning generation", e);
            planningRepository.delete(newPlanning);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to start planning generation");
        }

        return PlanningDto.fromEntity(newPlanning);
    }

    public PlanningDto updatePlanning(String planningId, PlanningUpdateDto planningUpdateDto) {
        Planning planning = planningRepository.findById(planningId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Planning not found"));

        if (planningUpdateDto.getStatus() != null) {
            planning.setStatus(planningUpdateDto.getStatus());

            Planning updatedPlanning = planningRepository.save(planning);

            return PlanningDto.fromEntity(updatedPlanning);
        }

        return PlanningDto.fromEntity(planning);
    }

    @Transactional
    public FinalizedPlanningDto finalizePlanning(String planningId, PlanningFinalizationDto planningFinalizationDto) {
        Planning planning = planningRepository.findById(planningId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Planning not found"));

        List<ShiftAssignment> existingShiftAssignments = shiftAssignmentRepository.findByPlanningId(planningId);

        if (!existingShiftAssignments.isEmpty()) {
            shiftAssignmentRepository.deleteAllInBatch(existingShiftAssignments);
        }

        List<ShiftAssignment> newShiftAssignments = planningFinalizationDto.getShiftAssignments()
                .stream()
                .map((dto) -> {
                    ShiftAssignment shiftAssignment = new ShiftAssignment();
                    shiftAssignment.setPlanning(planning);
                    shiftAssignment.setFirefighterId(dto.getFirefighterId());
                    shiftAssignment.setWeekday(dto.getWeekday());
                    shiftAssignment.setShiftType(dto.getShiftType());
                    return shiftAssignment;
                })
                .toList();

        shiftAssignmentRepository.saveAll(newShiftAssignments);

        planning.setStatus(PlanningStatus.FINALIZED);

        Planning updatedPlanning = planningRepository.save(planning);

        FinalizedPlanningDto finalizedPlanningDto = new FinalizedPlanningDto();
        finalizedPlanningDto.setPlanning(PlanningDto.fromEntity(updatedPlanning));
        finalizedPlanningDto.setShiftAssignments(new ArrayList<>());

        return finalizedPlanningDto;
    }

    public PlanningDto regeneratePlanning(String userId, UserRole userRole, String planningId) {
        Planning planning = planningRepository.findById(planningId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Planning not found"));

        if (userRole == UserRole.PLANNING_MANAGER) {
            ensureUserIsFromStation(userId, planning.getStationId());
        }

        planning.setStatus(PlanningStatus.GENERATING);

        Planning updatedPlanning = planningRepository.save(planning);

        try {
            startPlanningGeneration(planningId);
        } catch (Exception e) {
            logger.error("Failed to start planning generation", e);
            planning.setStatus(PlanningStatus.FINALIZED);
            planningRepository.save(planning);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to start planning generation");
        }

        return PlanningDto.fromEntity(updatedPlanning);
    }

    public void deletePlanning(String userId, UserRole userRole, String planningId) {
        Planning planning = planningRepository.findById(planningId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Planning not found"));

        if (userRole == UserRole.PLANNING_MANAGER) {
            ensureUserIsFromStation(userId, planning.getStationId());
        }

        planningRepository.delete(planning);
    }

    public void deleteShiftAssignmentsByPlanningId(String userId, UserRole userRole, String planningId) {
        Planning planning = planningRepository.findById(planningId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Planning not found"));

        if (userRole == UserRole.PLANNING_MANAGER) {
            ensureUserIsFromStation(userId, planning.getStationId());
        }

        List<ShiftAssignment> shiftAssignments = shiftAssignmentRepository.findByPlanningId(planningId);

        shiftAssignmentRepository.deleteAllInBatch(shiftAssignments);
    }

    private void startPlanningGeneration(String planningId) {
        restTemplate.postForEntity(planningEngineApiBaseUrl + "/planning/" + planningId + "/generate", null, Void.class);
    }

    private void ensureUserIsFromStation(String userId, String stationId) throws ApiException {
        UserDto user = accountsClient.getUserById(userId);
        if (!stationId.equals(user.getStationId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You are not authorized to perform this action for the specified station");
        }
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
