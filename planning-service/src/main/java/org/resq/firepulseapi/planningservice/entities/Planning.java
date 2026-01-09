package org.resq.firepulseapi.planningservice.entities;

import io.github.thibaultmeyer.cuid.CUID;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.resq.firepulseapi.planningservice.entities.enums.PlanningStatus;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "\"Planning\"", schema = "planning", indexes = {
        @Index(name = "Planning_year_weekNumber_stationId_key", columnList = "year, weekNumber, stationId", unique = true)
})
public class Planning {
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
    @Column(name = "status", columnDefinition = "planning.\"PlanningStatus\"", nullable = false)
    private PlanningStatus status;

    @Column(name = "\"stationId\"", nullable = false)
    private String stationId;

    @OneToMany(mappedBy = "planning")
    private Set<ShiftAssignment> shiftAssignments = new LinkedHashSet<>();

    @PrePersist
    @PreUpdate
    public void updateTimestamps() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        updatedAt = Instant.now();
    }
}
