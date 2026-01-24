package org.resq.firepulseapi.accountsservice.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.resq.firepulseapi.accountsservice.entities.enums.UserRole;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsersFilters {
    private String emailContains;
    private Set<UserRole> roles;
    private Set<@NotBlank(message = "Station ID cannot be blank") String> stationIds;
}
