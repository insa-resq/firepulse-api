package org.resq.firepulseapi.planningservice.dtos;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.resq.firepulseapi.planningservice.entities.enums.Weekday;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvailabilitySlotCreationDto {
    @Positive(message = "Year must be a positive integer")
    @NotNull(message = "Year is required")
    private Integer year;

    @NotNull(message = "Week number is required")
    private Integer weekNumber;

    @NotNull(message = "Weekday is required")
    private Weekday weekday;

    @NotNull(message = "Availability status is required")
    private Boolean isAvailable;

    @AssertTrue(message = "Week number must be between 1 and 53")
    private boolean isWeekNumberValid() {
        return weekNumber != null && weekNumber >= 1 && weekNumber <= 53;
    }
}
