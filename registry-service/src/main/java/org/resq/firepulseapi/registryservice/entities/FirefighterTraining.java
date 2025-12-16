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
    @Column(name = "ppbe", nullable = false)
    private Boolean ppbe = false;

    @ColumnDefault("false")
    @Column(name = "inc", nullable = false)
    private Boolean inc = false;

    @ColumnDefault("false")
    @Column(name = "\"roadRescue\"", nullable = false)
    private Boolean roadRescue = false;

    @ColumnDefault("false")
    @Column(name = "\"fiSpv\"", nullable = false)
    private Boolean fiSpv = false;

    @ColumnDefault("false")
    @Column(name = "\"teamLeader\"", nullable = false)
    private Boolean teamLeader = false;

    @ColumnDefault("false")
    @Column(name = "ca1e", nullable = false)
    private Boolean ca1e = false;

    @ColumnDefault("false")
    @Column(name = "cate", nullable = false)
    private Boolean cate = false;

    @ColumnDefault("false")
    @Column(name = "cdg", nullable = false)
    private Boolean cdg = false;

    @ColumnDefault("false")
    @Column(name = "cod0", nullable = false)
    private Boolean cod0 = false;

    @ColumnDefault("false")
    @Column(name = "cod1", nullable = false)
    private Boolean cod1 = false;

    @ColumnDefault("false")
    @Column(name = "\"permitB\"", nullable = false)
    private Boolean permitB = false;

    @ColumnDefault("false")
    @Column(name = "\"permitC\"", nullable = false)
    private Boolean permitC = false;

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
