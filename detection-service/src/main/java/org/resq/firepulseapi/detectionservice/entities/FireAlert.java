package org.resq.firepulseapi.detectionservice.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.type.SqlTypes;
import org.resq.firepulseapi.detectionservice.entities.enums.AlertStatus;
import org.resq.firepulseapi.detectionservice.entities.enums.FireSeverity;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "\"FireAlert\"", schema = "detection", indexes = {
        @Index(name = "FireAlert_createdAt_idx", columnList = "createdAt"),
        @Index(name = "FireAlert_severity_idx", columnList = "severity"),
        @Index(name = "FireAlert_status_idx", columnList = "status")
})
public class FireAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Integer id;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "\"createdAt\"", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "\"updatedAt\"", nullable = false)
    private Instant updatedAt;

    @Column(name = "description", nullable = false, length = Integer.MAX_VALUE)
    private String description;

    @Column(name = "confidence", nullable = false)
    private Double confidence;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "severity", columnDefinition = "detection.\"FireSeverity\"", nullable = false)
    private FireSeverity severity;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @ColumnDefault("'NEW'")
    @Column(name = "status", columnDefinition = "detection.\"AlertStatus\"", nullable = false)
    private AlertStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "\"imageId\"")
    private Image image;
}
