package org.resq.firepulseapi.registryservice.services;

import jakarta.persistence.criteria.Predicate;
import org.resq.firepulseapi.registryservice.dtos.FireStationDto;
import org.resq.firepulseapi.registryservice.dtos.FireStationOverviewDto;
import org.resq.firepulseapi.registryservice.dtos.FireStationsFilters;
import org.resq.firepulseapi.registryservice.entities.FireStation;
import org.resq.firepulseapi.registryservice.exceptions.ApiException;
import org.resq.firepulseapi.registryservice.repositories.FireStationRepository;
import org.resq.firepulseapi.registryservice.repositories.VehicleRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FireStationService {
    private final FireStationRepository fireStationRepository;
    private final VehicleRepository vehicleRepository;

    public FireStationService(FireStationRepository fireStationRepository, VehicleRepository vehicleRepository) {
        this.fireStationRepository = fireStationRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public List<FireStationDto> getAllFireStations(FireStationsFilters filters) {
        Specification<FireStation> specification = buildSpecificationFromFilters(filters);
        return fireStationRepository.findAll(specification)
                .stream()
                .map(FireStationDto::fromEntity)
                .toList();
    }

    public FireStationDto getFireStationById(String stationId) {
        return fireStationRepository.findById(stationId)
                .map(FireStationDto::fromEntity)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Fire station not found"));
    }

    public FireStationOverviewDto getFireStationOverview(String stationId) {
        FireStation fireStation = fireStationRepository.findById(stationId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Fire station not found"));

        List<FireStationOverviewDto.AvailableVehicleDto> availableVehiclesDto = vehicleRepository.findAll(
                (root, query, criteriaBuilder) -> criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("station").get("id"), fireStation.getId()),
                        criteriaBuilder.greaterThan(root.get("availableCount"), 0)
                )
        )
                .stream()
                .map(FireStationOverviewDto.AvailableVehicleDto::fromEntity)
                .toList();

        FireStationOverviewDto overviewDto = new FireStationOverviewDto();
        overviewDto.setAvailableVehicles(availableVehiclesDto);

        return overviewDto;
    }

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
