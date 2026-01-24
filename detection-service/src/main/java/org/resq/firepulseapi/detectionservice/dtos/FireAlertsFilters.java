package org.resq.firepulseapi.detectionservice.dtos;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.resq.firepulseapi.detectionservice.entities.enums.AlertStatus;
import org.resq.firepulseapi.detectionservice.entities.enums.FireSeverity;

import java.time.Instant;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FireAlertsFilters {
    private Set<FireSeverity> severities;
    private Set<AlertStatus> statuses;
    private Instant createdAt;
    private Instant createdAtFrom;
    private Instant createdAtTo;
    @Pattern(regexp = "^(?!\\s*$).+", message = "Image ID cannot be blank")
    private String imageId;

    @AssertTrue(message = "Date range and fixed value cannot be set simultaneously for createdAt")
    private boolean isDateValid() {
        return createdAt == null || (createdAtFrom == null && createdAtTo == null);
    }

    @AssertTrue(message = "From date must be before To date for createdAt")
    private boolean isDateRangeValid() {
        return createdAtFrom == null || createdAtTo == null || createdAtFrom.isBefore(createdAtTo);
    }
}
