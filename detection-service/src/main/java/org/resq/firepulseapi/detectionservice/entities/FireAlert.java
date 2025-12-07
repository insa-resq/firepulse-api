package org.resq.firepulseapi.detectionservice.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
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
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "\"createdAt\"", nullable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "\"updatedAt\"", nullable = false)
    private Instant updatedAt;

    @NotNull
    @Column(name = "description", nullable = false, length = Integer.MAX_VALUE)
    private String description;

    @NotNull
    @Column(name = "confidence", nullable = false)
    private Double confidence;

    @NotNull
    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @NotNull
    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false)
    private FireSeverity severity;

    @NotNull
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'NEW'")
    @Column(name = "status", nullable = false)
    private AlertStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "\"imageId\"")
    private Image image;
}
