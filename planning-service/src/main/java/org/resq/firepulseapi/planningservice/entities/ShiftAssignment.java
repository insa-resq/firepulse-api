package org.resq.firepulseapi.planningservice.entities;

import io.github.thibaultmeyer.cuid.CUID;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.type.SqlTypes;
import org.resq.firepulseapi.planningservice.entities.enums.ShiftType;
import org.resq.firepulseapi.planningservice.entities.enums.Weekday;

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
    @Column(name = "id", nullable = false, updatable = false, length = Integer.MAX_VALUE)
    private String id = String.valueOf(CUID.randomCUID2());

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "\"createdAt\"", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "\"updatedAt\"", nullable = false)
    private Instant updatedAt;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "weekday", columnDefinition = "planning.\"Weekday\"", nullable = false)
    private Weekday weekday;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "\"shiftType\"", columnDefinition = "planning.\"ShiftType\"", nullable = false)
    private ShiftType shiftType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "\"firefighterId\"", nullable = false)
    private Firefighter firefighter;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "\"planningId\"", nullable = false)
    private Planning planning;
}
