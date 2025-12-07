package org.resq.firepulseapi.stationlogixservice.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "\"stationId\"", nullable = false)
    private FireStation station;

    @OneToMany(mappedBy = "planning")
    private Set<ShiftAssignment> shiftAssignments = new LinkedHashSet<>();
}
