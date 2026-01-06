package org.resq.firepulseapi.planningservice.services;

import jakarta.persistence.criteria.Predicate;
import org.resq.firepulseapi.planningservice.dtos.AvailabilitySlotCreationDto;
import org.resq.firepulseapi.planningservice.dtos.AvailabilitySlotDto;
import org.resq.firepulseapi.planningservice.dtos.AvailabilitySlotUpdateDto;
import org.resq.firepulseapi.planningservice.dtos.AvailabilitySlotsFilters;
import org.resq.firepulseapi.planningservice.entities.AvailabilitySlot;
import org.resq.firepulseapi.planningservice.exceptions.ApiException;
import org.resq.firepulseapi.planningservice.repositories.AvailabilitySlotRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AvailabilitySlotService {
    private final AvailabilitySlotRepository availabilitySlotRepository;

    public AvailabilitySlotService(AvailabilitySlotRepository availabilitySlotRepository) {
        this.availabilitySlotRepository = availabilitySlotRepository;
    }

    public List<AvailabilitySlotDto> getAvailabilitySlots(AvailabilitySlotsFilters filters) {
        Specification<AvailabilitySlot> specification = buildSpecificationFromFilters(filters);
        return availabilitySlotRepository.findAll(specification)
                .stream()
                .map(AvailabilitySlotDto::fromEntity)
                .toList();
    }

    public AvailabilitySlotDto getAvailabilitySlotById(String availabilitySlotId) {
        return availabilitySlotRepository.findById(availabilitySlotId)
                .map(AvailabilitySlotDto::fromEntity)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Availability slot not found"));
    }

    public AvailabilitySlotDto createAvailabilitySlot(String firefighterId, AvailabilitySlotCreationDto availabilitySlotCreationDto) {
        Specification<AvailabilitySlot> specification = buildSpecificationFromFilters(
                new AvailabilitySlotsFilters(
                        availabilitySlotCreationDto.getYear(),
                        availabilitySlotCreationDto.getWeekNumber(),
                        availabilitySlotCreationDto.getWeekday(),
                        firefighterId
                )
        );

        List<AvailabilitySlot> existingSlots = availabilitySlotRepository.findAll(specification);

        if (!existingSlots.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Availability slot for the specified time period already exists");
        }

        AvailabilitySlot availabilitySlot = new AvailabilitySlot();

        availabilitySlot.setYear(availabilitySlotCreationDto.getYear());
        availabilitySlot.setWeekNumber(availabilitySlotCreationDto.getWeekNumber());
        availabilitySlot.setWeekday(availabilitySlotCreationDto.getWeekday());
        availabilitySlot.setIsAvailable(availabilitySlotCreationDto.getIsAvailable());
        availabilitySlot.setFirefighterId(firefighterId);

        AvailabilitySlot savedAvailabilitySlot = availabilitySlotRepository.save(availabilitySlot);

        return AvailabilitySlotDto.fromEntity(savedAvailabilitySlot);
    }

    public AvailabilitySlotDto updateAvailabilitySlot(
            String availabilitySlotId,
            AvailabilitySlotUpdateDto availabilitySlotUpdateDto
    ) {
        AvailabilitySlot availabilitySlot = availabilitySlotRepository.findById(availabilitySlotId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Availability slot not found"));

        if (availabilitySlotUpdateDto.getIsAvailable() != null) {
            availabilitySlot.setIsAvailable(availabilitySlotUpdateDto.getIsAvailable());

            AvailabilitySlot updatedAvailabilitySlot = availabilitySlotRepository.save(availabilitySlot);

            return AvailabilitySlotDto.fromEntity(updatedAvailabilitySlot);
        }

        return AvailabilitySlotDto.fromEntity(availabilitySlot);
    }

    private Specification<AvailabilitySlot> buildSpecificationFromFilters(AvailabilitySlotsFilters filters) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filters.getYear() != null) {
                predicates.add(criteriaBuilder.equal(root.get("year"), filters.getYear()));
            }

            if (filters.getWeekNumber() != null) {
                predicates.add(criteriaBuilder.equal(root.get("weekNumber"), filters.getWeekNumber()));
            }

            if (filters.getWeekday() != null) {
                predicates.add(criteriaBuilder.equal(root.get("weekday"), filters.getWeekday()));
            }

            if (filters.getFirefighterId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("firefighterId"), filters.getFirefighterId()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
