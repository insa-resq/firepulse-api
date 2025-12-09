package org.resq.firepulseapi.accountsservice.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenDto {
    @NotBlank(message = "Token cannot be blank")
    @NotNull(message = "Token is required")
    private String token;
}
