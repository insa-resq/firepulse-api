package org.resq.firepulseapi.brigadeflowservice.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "\"firefighterId\"", nullable = false)
    private Firefighter firefighter;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "ppbe", nullable = false)
    private Boolean ppbe = false;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "inc", nullable = false)
    private Boolean inc = false;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "\"roadRescue\"", nullable = false)
    private Boolean roadRescue = false;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "\"fiSpv\"", nullable = false)
    private Boolean fiSpv = false;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "\"teamLeader\"", nullable = false)
    private Boolean teamLeader = false;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "ca1e", nullable = false)
    private Boolean ca1e = false;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "cate", nullable = false)
    private Boolean cate = false;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "cdg", nullable = false)
    private Boolean cdg = false;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "cod0", nullable = false)
    private Boolean cod0 = false;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "cod1", nullable = false)
    private Boolean cod1 = false;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "\"permitB\"", nullable = false)
    private Boolean permitB = false;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "\"permitC\"", nullable = false)
    private Boolean permitC = false;
}
