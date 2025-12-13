package org.resq.firepulseapi.planningservice.entities;

import io.github.thibaultmeyer.cuid.CUID;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.type.SqlTypes;
import org.resq.firepulseapi.planningservice.entities.enums.Weekday;

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
    @Column(name = "id", nullable = false, updatable = false, length = Integer.MAX_VALUE)
    private String id = String.valueOf(CUID.randomCUID2());

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "\"createdAt\"", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "\"updatedAt\"", nullable = false)
    private Instant updatedAt;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "\"weekNumber\"", nullable = false)
    private Integer weekNumber;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "weekday", columnDefinition = "planning.\"Weekday\"", nullable = false)
    private Weekday weekday;

    @Column(name = "\"isAvailable\"", nullable = false)
    private Boolean isAvailable = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "\"firefighterId\"", nullable = false)
    private Firefighter firefighter;

    @PrePersist
    @PreUpdate
    public void updateTimestamps() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        updatedAt = Instant.now();
    }
}
