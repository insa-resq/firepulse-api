package org.resq.firepulseapi.registryservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FirefighterTrainingFilters {
    private String firefighterId;
    private String permitType; // "permitB", "permitC", "permitAircraft", etc.
    private Boolean hasPermit; // true = a le permis, false = n'a pas le permis
}