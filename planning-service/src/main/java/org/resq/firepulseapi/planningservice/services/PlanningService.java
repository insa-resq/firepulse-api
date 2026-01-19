package org.resq.firepulseapi.planningservice.services;

import feign.FeignException;
import jakarta.persistence.criteria.Predicate;
import org.resq.firepulseapi.planningservice.clients.AccountsClient;
import org.resq.firepulseapi.planningservice.clients.RegistryClient;
import org.resq.firepulseapi.planningservice.dtos.*;
import org.resq.firepulseapi.planningservice.entities.Planning;
import org.resq.firepulseapi.planningservice.entities.ShiftAssignment;
import org.resq.firepulseapi.planningservice.entities.VehicleAvailability;
import org.resq.firepulseapi.planningservice.entities.enums.PlanningStatus;
import org.resq.firepulseapi.planningservice.entities.enums.UserRole;
import org.resq.firepulseapi.planningservice.entities.enums.Weekday;
import org.resq.firepulseapi.planningservice.exceptions.ApiException;
import org.resq.firepulseapi.planningservice.repositories.PlanningRepository;
import org.resq.firepulseapi.planningservice.repositories.ShiftAssignmentRepository;
import org.resq.firepulseapi.planningservice.repositories.VehicleAvailabilityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PlanningService {
    private static final Logger logger = LoggerFactory.getLogger(PlanningService.class);
    private final PlanningRepository planningRepository;
    private final ShiftAssignmentRepository shiftAssignmentRepository;
    private final VehicleAvailabilityRepository vehicleAvailabilityRepository;
    private final AccountsClient accountsClient;
    private final RegistryClient registryClient;
    private final RestTemplate restTemplate;

    @Value("${http.planning-engine-api.base-url}")
    private String planningEngineApiBaseUrl;

    public PlanningService(
            PlanningRepository planningRepository,
            ShiftAssignmentRepository shiftAssignmentRepository,
            VehicleAvailabilityRepository vehicleAvailabilityRepository,
            AccountsClient accountsClient,
            RegistryClient registryClient,
            RestTemplate restTemplate
    ) {
        this.planningRepository = planningRepository;
        this.shiftAssignmentRepository = shiftAssignmentRepository;
        this.vehicleAvailabilityRepository = vehicleAvailabilityRepository;
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

    @Transactional
    public FinalizedPlanningDto finalizePlanning(String planningId, PlanningFinalizationDto planningFinalizationDto) {
        Planning planning = planningRepository.findById(planningId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Planning not found"));

        Map<String, Set<Weekday>> vehicleWeekdaysMap = planningFinalizationDto.getVehicleAvailabilities()
                .stream()
                .collect(Collectors.groupingBy(
                        PlanningFinalizationDto.VehicleAvailabilityCreationDto::getVehicleId,
                        Collectors.mapping(PlanningFinalizationDto.VehicleAvailabilityCreationDto::getWeekday, Collectors.toSet())
                ));

        vehicleWeekdaysMap.forEach((vehicleId, weekdays) -> {
            if (!weekdays.containsAll(Set.of(Weekday.values()))) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Vehicle ID " + vehicleId + " does not have availabilities for all weekdays");
            }

            if (weekdays.size() != Weekday.values().length) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Vehicle ID " + vehicleId + " has duplicate weekdays");
            }
        });

        Map<String, VehicleDto> vehiclesMap = registryClient.getVehicles(planning.getStationId())
                .stream()
                .collect(Collectors.toMap(VehicleDto::getId, vehicle -> vehicle));

        Set<String> fireStationVehiclesIds = vehiclesMap.keySet();

        List<String> nonExistingVehicleIds = planningFinalizationDto.getVehicleAvailabilities()
                .stream()
                .map(PlanningFinalizationDto.VehicleAvailabilityCreationDto::getVehicleId)
                .filter(vehicleId -> !fireStationVehiclesIds.contains(vehicleId))
                .toList();

        if (!nonExistingVehicleIds.isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "The following vehicles are not found: " + String.join(", ", nonExistingVehicleIds));
        }

        List<String> invalidVehicleAvailabilities = planningFinalizationDto.getVehicleAvailabilities()
                .stream()
                .filter(vehicleAvailabilityDto ->
                        vehicleAvailabilityDto.getAvailableCount() >
                                vehiclesMap.get(vehicleAvailabilityDto.getVehicleId()).getTotalCount()
                )
                .map(PlanningFinalizationDto.VehicleAvailabilityCreationDto::getVehicleId)
                .toList();

        if (!invalidVehicleAvailabilities.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Vehicle availabilities exceed total counts for the following vehicles: " + String.join(", ", invalidVehicleAvailabilities));
        }

        Set<String> shiftAssignmentTriplets = planningFinalizationDto.getShiftAssignments()
                .stream()
                .map(dto -> dto.getFirefighterId() + "-" + dto.getWeekday() + "-" + dto.getShiftType())
                .collect(Collectors.toSet());

        if (shiftAssignmentTriplets.size() != planningFinalizationDto.getShiftAssignments().size()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Duplicate shift assignments found");
        }

        List<FirefighterDto> firefighters = registryClient.getFirefighters(planning.getStationId());

        Set<String> fireStationFirefightersIds = firefighters.stream()
                .map(FirefighterDto::getId)
                .collect(Collectors.toSet());

        List<String> nonExistingFirefighterIds = planningFinalizationDto.getShiftAssignments()
                .stream()
                .map(PlanningFinalizationDto.ShiftAssignmentCreationDto::getFirefighterId)
                .filter(firefighterId -> !fireStationFirefightersIds.contains(firefighterId))
                .toList();

        if (!nonExistingFirefighterIds.isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "The following firefighters are not found: " + String.join(", ", nonExistingFirefighterIds));
        }

        List<ShiftAssignment> existingShiftAssignments = shiftAssignmentRepository.findByPlanningId(planningId);

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

        List<VehicleAvailability> newVehicleAvailabilities = planningFinalizationDto.getVehicleAvailabilities()
                .stream()
                .map((dto) -> {
                    VehicleAvailability vehicleAvailability = new VehicleAvailability();
                    vehicleAvailability.setVehicleId(dto.getVehicleId());
                    vehicleAvailability.setWeekday(dto.getWeekday());
                    vehicleAvailability.setAvailableCount(dto.getAvailableCount());
                    return vehicleAvailability;
                })
                .toList();

        if (!existingShiftAssignments.isEmpty()) {
            shiftAssignmentRepository.deleteAllInBatch(existingShiftAssignments);
        }

        shiftAssignmentRepository.saveAll(newShiftAssignments);

        vehicleAvailabilityRepository.deleteAllByVehicleIdIn(
                newVehicleAvailabilities.stream()
                        .map(VehicleAvailability::getVehicleId)
                        .collect(Collectors.toSet())
        );

        vehicleAvailabilityRepository.saveAll(newVehicleAvailabilities);

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
        ResponseEntity<?> response = restTemplate.postForEntity(
                planningEngineApiBaseUrl + "/planning/" + planningId + "/generate",
                null,
                Object.class
        );

        logger.info("Planning generation started successfully for planning {}: {}", planningId, response.getBody());
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
