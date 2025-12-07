package org.resq.firepulseapi.planningservice.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.resq.firepulseapi.planningservice.entities.enums.FirefighterRank;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "\"Firefighter\"", schema = "registry", indexes = {
        @Index(name = "Firefighter_userId_key", columnList = "userId", unique = true),
        @Index(name = "Firefighter_rank_idx", columnList = "rank"),
        @Index(name = "Firefighter_stationId_idx", columnList = "stationId")
})
public class Firefighter {
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
    @Column(name = "\"firstName\"", nullable = false, length = Integer.MAX_VALUE)
    private String firstName;

    @NotNull
    @Column(name = "\"lastName\"", nullable = false, length = Integer.MAX_VALUE)
    private String lastName;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "rank", nullable = false)
    private FirefighterRank rank;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "\"stationId\"", nullable = false)
    private FireStation station;

    @OneToMany
    @JoinColumn(name = "\"firefighterId\"")
    private Set<AvailabilitySlot> availabilitySlots = new LinkedHashSet<>();

    @OneToMany
    @JoinColumn(name = "\"firefighterId\"")
    private Set<ShiftAssignment> shiftAssignments = new LinkedHashSet<>();
}
