package org.resq.firepulseapi.planningservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.resq.firepulseapi.planningservice.entities.AvailabilitySlot;
import org.resq.firepulseapi.planningservice.entities.enums.Weekday;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvailabilitySlotDto {
    private String id;
    private Instant createdAt;
    private Instant updatedAt;
    private Integer year;
    private Integer weekNumber;
    private Weekday weekday;
    private Boolean isAvailable;
    private String firefighterId;

    public static AvailabilitySlotDto fromEntity(AvailabilitySlot slot) {
        AvailabilitySlotDto dto = new AvailabilitySlotDto();
        dto.setId(slot.getId());
        dto.setCreatedAt(slot.getCreatedAt());
        dto.setUpdatedAt(slot.getUpdatedAt());
        dto.setYear(slot.getYear());
        dto.setWeekNumber(slot.getWeekNumber());
        dto.setWeekday(slot.getWeekday());
        dto.setIsAvailable(slot.getIsAvailable());
        dto.setFirefighterId(slot.getFirefighterId());
        return dto;
    }
}
