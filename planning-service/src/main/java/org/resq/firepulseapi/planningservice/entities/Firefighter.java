package org.resq.firepulseapi.planningservice.entities;

import io.github.thibaultmeyer.cuid.CUID;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.type.SqlTypes;
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
    @Column(name = "id", nullable = false, updatable = false, length = Integer.MAX_VALUE)
    private String id = String.valueOf(CUID.randomCUID2());

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "\"createdAt\"", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "\"updatedAt\"", nullable = false)
    private Instant updatedAt;

    @Column(name = "\"firstName\"", nullable = false, length = Integer.MAX_VALUE)
    private String firstName;

    @Column(name = "\"lastName\"", nullable = false, length = Integer.MAX_VALUE)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "rank", columnDefinition = "registry.\"FirefighterRank\"", nullable = false)
    private FirefighterRank rank;

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

    @PrePersist
    @PreUpdate
    public void updateTimestamps() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        updatedAt = Instant.now();
    }
}
