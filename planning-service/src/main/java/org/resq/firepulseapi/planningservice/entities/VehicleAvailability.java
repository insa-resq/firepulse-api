package org.resq.firepulseapi.planningservice.entities;

import io.github.thibaultmeyer.cuid.CUID;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.resq.firepulseapi.planningservice.entities.enums.Weekday;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "\"VehicleAvailability\"", schema = "planning", indexes = {
        @Index(name = "VehicleAvailability_vehicleId_weekday_key", columnList = "vehicleId, weekday", unique = true)
})
public class VehicleAvailability {
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
    @Column(name = "weekday", columnDefinition = "registry.\"Weekday\"", nullable = false)
    private Weekday weekday;

    @ColumnDefault("0")
    @Column(name = "\"availableCount\"", nullable = false)
    private Integer availableCount;

    @ColumnDefault("0")
    @Column(name = "\"bookedCount\"", nullable = false)
    private Integer bookedCount;

    @Column(name = "\"vehicleId\"", nullable = false)
    private String vehicleId;

    @PrePersist
    @PreUpdate
    public void updateTimestamps() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        updatedAt = Instant.now();
    }
}
