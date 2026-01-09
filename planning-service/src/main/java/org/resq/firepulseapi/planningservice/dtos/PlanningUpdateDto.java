package org.resq.firepulseapi.planningservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.resq.firepulseapi.planningservice.entities.enums.PlanningStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanningUpdateDto {
    private PlanningStatus status;
}
