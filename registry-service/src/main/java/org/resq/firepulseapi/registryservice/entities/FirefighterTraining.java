package org.resq.firepulseapi.registryservice.entities;

import io.github.thibaultmeyer.cuid.CUID;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.*;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "\"FirefighterTraining\"", schema = "registry", indexes = {
        @Index(name = "FirefighterTraining_firefighterId_key", columnList = "firefighterId", unique = true)
})
public class FirefighterTraining {
    @Id
    @Column(name = "id", nullable = false, updatable = false, length = Integer.MAX_VALUE)
    private String id = String.valueOf(CUID.randomCUID2());

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "\"createdAt\"", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "\"updatedAt\"", nullable = false)
    private Instant updatedAt;

    @ColumnDefault("false")
    @Column(name = "\"permitB\"", nullable = false)
    private Boolean permitB = false;

    @ColumnDefault("false")
    @Column(name = "\"permitC\"", nullable = false)
    private Boolean permitC = false;

    @ColumnDefault("false")
    @Column(name = "\"permitAircraft\"", nullable = false)
    private Boolean permitAircraft = false;

    @ColumnDefault("false")
    @Column(name = "suap", nullable = false)
    private Boolean suap = false;

    @ColumnDefault("false")
    @Column(name = "inc", nullable = false)
    private Boolean inc = false;

    @ColumnDefault("false")
    @Column(name = "\"smallTeamLeader\"", nullable = false)
    private Boolean smallTeamLeader = false;

    @ColumnDefault("false")
    @Column(name = "\"mediumTeamLeader\"", nullable = false)
    private Boolean mediumTeamLeader = false;

    @ColumnDefault("false")
    @Column(name = "\"largeTeamLeader\"", nullable = false)
    private Boolean largeTeamLeader = false;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
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
