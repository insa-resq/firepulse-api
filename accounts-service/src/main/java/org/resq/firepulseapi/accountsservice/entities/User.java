package org.resq.firepulseapi.accountsservice.entities;

import jakarta.persistence.*;
import io.github.thibaultmeyer.cuid.CUID;
import lombok.*;
import org.hibernate.annotations.*;
import org.hibernate.type.SqlTypes;
import org.hibernate.validator.constraints.URL;
import org.resq.firepulseapi.accountsservice.entities.enums.UserRole;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "\"User\"", schema = "accounts", indexes = {
        @Index(name = "User_email_key", columnList = "email", unique = true),
        @Index(name = "User_role_idx", columnList = "role"),
        @Index(name = "User_stationId_idx", columnList = "stationId")
})
public class User {
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

    @Column(name = "email", nullable = false, unique = true, length = Integer.MAX_VALUE)
    private String email;

    @Column(name = "password", nullable = false, length = Integer.MAX_VALUE)
    private String password;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "role", columnDefinition = "accounts.\"UserRole\"", nullable = false)
    private UserRole role;

    @URL
    @Column(name = "\"avatarUrl\"", nullable = false)
    private String avatarUrl;

    @Column(name = "\"stationId\"", nullable = false)
    private String stationId;
}
