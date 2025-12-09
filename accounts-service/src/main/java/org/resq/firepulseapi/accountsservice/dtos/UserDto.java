package org.resq.firepulseapi.accountsservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.resq.firepulseapi.accountsservice.entities.User;
import org.resq.firepulseapi.accountsservice.entities.enums.UserRole;

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

    public static UserDto fromEntity(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setStationId(user.getStationId());
        return dto;
    }
}
