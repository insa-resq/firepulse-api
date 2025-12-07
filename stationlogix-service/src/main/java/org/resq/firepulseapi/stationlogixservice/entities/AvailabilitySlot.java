package org.resq.firepulseapi.stationlogixservice.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.resq.firepulseapi.stationlogixservice.entities.enums.Weekday;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "\"AvailabilitySlot\"", schema = "planning", indexes = {
        @Index(name = "AvailabilitySlot_firefighterId_year_weekNumber_weekday_key", columnList = "firefighterId, year, weekNumber, weekday", unique = true),
        @Index(name = "AvailabilitySlot_year_weekNumber_weekday_isAvailable_idx", columnList = "year, weekNumber, weekday, isAvailable")
})
public class AvailabilitySlot {
    @Id
    @Column(name = "id", nullable = false, length = Integer.MAX_VALUE)
    private String id;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "\"createdAt\"", nullable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "\"updatedAt\"", nullable = false)
    private Instant updatedAt;

    @NotNull
    @Column(name = "year", nullable = false)
    private Integer year;

    @NotNull
    @Column(name = "\"weekNumber\"", nullable = false)
    private Integer weekNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "weekday", nullable = false)
    private Weekday weekday;

    @NotNull
    @Column(name = "\"isAvailable\"", nullable = false)
    private Boolean isAvailable = false;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "\"firefighterId\"", nullable = false)
    private Firefighter firefighter;
}
