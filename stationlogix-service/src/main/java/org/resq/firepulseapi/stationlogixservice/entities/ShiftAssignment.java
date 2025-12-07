package org.resq.firepulseapi.stationlogixservice.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.resq.firepulseapi.stationlogixservice.entities.enums.ShiftType;
import org.resq.firepulseapi.stationlogixservice.entities.enums.Weekday;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "\"ShiftAssignment\"", schema = "planning", indexes = {
        @Index(name = "ShiftAssignment_planningId_weekday_firefighterId_key", columnList = "planningId, weekday, firefighterId", unique = true)
})
public class ShiftAssignment {
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
    @Enumerated(EnumType.STRING)
    @Column(name = "weekday", nullable = false)
    private Weekday weekday;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "\"shiftType\"", nullable = false)
    private ShiftType shiftType;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "\"firefighterId\"", nullable = false)
    private Firefighter firefighter;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "\"planningId\"", nullable = false)
    private Planning planning;
}
