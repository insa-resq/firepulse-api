package org.resq.firepulseapi.brigadeflowservice.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.resq.firepulseapi.brigadeflowservice.entities.enums.VehicleType;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "\"Vehicle\"", schema = "registry", indexes = {
        @Index(name = "Vehicle_type_idx", columnList = "type"),
        @Index(name = "Vehicle_stationId_idx", columnList = "stationId")
})
public class Vehicle {
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
    @Column(name = "type", nullable = false)
    private VehicleType type;

    @NotNull
    @Column(name = "\"totalCount\"", nullable = false)
    private Integer totalCount;

    @NotNull
    @Column(name = "\"availableCount\"", nullable = false)
    private Integer availableCount;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "\"stationId\"", nullable = false)
    private FireStation station;
}
