package org.resq.firepulseapi.accountsservice.dtos;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileUpdateDto {
    @Email(message = "Invalid email format")
    private String email;
}
