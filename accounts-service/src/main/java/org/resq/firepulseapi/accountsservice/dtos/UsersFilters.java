package org.resq.firepulseapi.accountsservice.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.resq.firepulseapi.accountsservice.entities.enums.UserRole;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsersFilters {
    private String emailContains;
    private List<UserRole> roles;
    private List<@NotBlank(message = "Station ID cannot be blank") String> stationIds;
}
