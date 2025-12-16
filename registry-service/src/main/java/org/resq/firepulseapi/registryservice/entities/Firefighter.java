package org.resq.firepulseapi.registryservice.entities;

import io.github.thibaultmeyer.cuid.CUID;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.*;
import org.hibernate.type.SqlTypes;
import org.resq.firepulseapi.registryservice.entities.enums.FirefighterRank;

import java.time.Instant;

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

    @Column(name = "\"userId\"", nullable = false)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "\"stationId\"", nullable = false)
    private FireStation station;

    @OneToOne(mappedBy = "firefighter")
    private FirefighterTraining firefighterTraining;

    @PrePersist
    @PreUpdate
    public void updateTimestamps() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        updatedAt = Instant.now();
    }
}
