package org.resq.firepulseapi.accountsservice.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStationUpdateDto {
    @NotBlank(message = "Station ID cannot be blank")
    @NotNull(message = "Station ID is required")
    private String stationId;
}
