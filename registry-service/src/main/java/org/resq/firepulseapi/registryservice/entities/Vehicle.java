package org.resq.firepulseapi.registryservice.entities;

import io.github.thibaultmeyer.cuid.CUID;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.*;
import org.hibernate.type.SqlTypes;
import org.resq.firepulseapi.registryservice.entities.enums.VehicleType;

import java.time.Instant;
import java.util.Map;

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
    @Column(name = "id", nullable = false, updatable = false, length = Integer.MAX_VALUE)
    private String id = String.valueOf(CUID.randomCUID2());

    @CreationTimestamp
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "\"createdAt\"", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "\"updatedAt\"", nullable = false)
    private Instant updatedAt;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "type", columnDefinition = "registry.\"VehicleType\"", nullable = false)
    private VehicleType type;

    @Column(name = "\"totalCount\"", nullable = false)
    private Integer totalCount;

    @Column(name = "\"availableCount\"", nullable = false)
    private Integer availableCount;

    @ColumnDefault("'{}'")
    @Column(name = "metadata", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> metadata;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "\"stationId\"", nullable = false)
    private FireStation station;
}
