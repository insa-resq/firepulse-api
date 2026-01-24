package org.resq.firepulseapi.planningservice.services;

import jakarta.persistence.criteria.Predicate;
import org.resq.firepulseapi.planningservice.dtos.AvailabilitySlotCreationDto;
import org.resq.firepulseapi.planningservice.dtos.AvailabilitySlotDto;
import org.resq.firepulseapi.planningservice.dtos.AvailabilitySlotUpdateDto;
import org.resq.firepulseapi.planningservice.dtos.AvailabilitySlotsFilters;
import org.resq.firepulseapi.planningservice.entities.AvailabilitySlot;
import org.resq.firepulseapi.planningservice.exceptions.ApiException;
import org.resq.firepulseapi.planningservice.repositories.AvailabilitySlotRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AvailabilitySlotService {
    private final AvailabilitySlotRepository availabilitySlotRepository;

    private static class CacheKey {
        public static final String AVAILABILITY_SLOT_BY_ID = "AVAILABILITY_SLOT_BY_ID";
        public static final String AVAILABILITY_SLOTS_LIST = "AVAILABILITY_SLOTS_LIST";
    }

    public AvailabilitySlotService(AvailabilitySlotRepository availabilitySlotRepository) {
        this.availabilitySlotRepository = availabilitySlotRepository;
    }

    @Cacheable(value = CacheKey.AVAILABILITY_SLOTS_LIST, key = "#filters")
    public List<AvailabilitySlotDto> getAvailabilitySlots(AvailabilitySlotsFilters filters) {
        Specification<AvailabilitySlot> specification = buildSpecificationFromFilters(filters);
        return availabilitySlotRepository.findAll(specification)
                .stream()
                .map(AvailabilitySlotDto::fromEntity)
                .toList();
    }

    @Cacheable(value = CacheKey.AVAILABILITY_SLOT_BY_ID, key = "#availabilitySlotId")
    public AvailabilitySlotDto getAvailabilitySlotById(String availabilitySlotId) {
        return availabilitySlotRepository.findById(availabilitySlotId)
                .map(AvailabilitySlotDto::fromEntity)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Availability slot not found"));
    }

    @Transactional
    @CacheEvict(value = CacheKey.AVAILABILITY_SLOTS_LIST, allEntries = true)
    public AvailabilitySlotDto createAvailabilitySlot(String firefighterId, AvailabilitySlotCreationDto availabilitySlotCreationDto) {
        Specification<AvailabilitySlot> specification = buildSpecificationFromFilters(
                new AvailabilitySlotsFilters(
                        availabilitySlotCreationDto.getYear(),
                        availabilitySlotCreationDto.getWeekNumber(),
                        availabilitySlotCreationDto.getWeekday(),
                        firefighterId
                )
        );

        if (availabilitySlotRepository.exists(specification)) {
            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "Availability slot already exists for the specified time period. Try updating it instead."
            );
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

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheKey.AVAILABILITY_SLOT_BY_ID, key = "#availabilitySlotId"),
            @CacheEvict(value = CacheKey.AVAILABILITY_SLOTS_LIST, allEntries = true)
    })
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

            if (filters.getFirefighterId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("firefighterId"), filters.getFirefighterId()));
            }

            if (filters.getYear() != null) {
                predicates.add(criteriaBuilder.equal(root.get("year"), filters.getYear()));
            }

            if (filters.getWeekNumber() != null) {
                predicates.add(criteriaBuilder.equal(root.get("weekNumber"), filters.getWeekNumber()));
            }

            if (filters.getWeekday() != null) {
                predicates.add(criteriaBuilder.equal(root.get("weekday"), filters.getWeekday()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
