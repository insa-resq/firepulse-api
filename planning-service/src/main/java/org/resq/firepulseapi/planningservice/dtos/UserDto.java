package org.resq.firepulseapi.planningservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.resq.firepulseapi.planningservice.entities.enums.UserRole;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String id;
    private Instant createdAt;
    private Instant updatedAt;
    private String email;
    private UserRole role;
    private String avatarUrl;
    private String stationId;
}
