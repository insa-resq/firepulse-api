package org.resq.firepulseapi.registryservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FireStationsFilters {
    private String nameContains;
}
