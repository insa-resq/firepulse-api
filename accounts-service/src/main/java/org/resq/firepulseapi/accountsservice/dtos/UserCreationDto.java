package org.resq.firepulseapi.accountsservice.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreationDto {
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email cannot be blank")
    @NotNull(message = "Email is required")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @NotNull(message = "Password is required")
    private String password;

    @NotBlank(message = "Station ID cannot be blank")
    @NotNull(message = "Station ID is required")
    private String stationId;
}
