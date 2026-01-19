package org.resq.firepulseapi.coordinationservice.clients;

import org.resq.firepulseapi.coordinationservice.configurations.FeignClientConfig;
import org.resq.firepulseapi.coordinationservice.dtos.VehicleAvailabilityDto;
import org.resq.firepulseapi.coordinationservice.dtos.VehicleAvailabilityUpdateDto;
import org.resq.firepulseapi.coordinationservice.entities.enums.Weekday;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "planning-service", configuration = FeignClientConfig.class)
public interface PlanningClient {
    @GetMapping("/vehicle-availabilities")
    List<VehicleAvailabilityDto> getVehicleAvailabilities(@RequestHeader("Authorization") String authenticationHeaderValue, @RequestParam List<String> vehicleIds, @RequestParam Weekday weekday);

    @PatchMapping("/vehicle-availabilities")
    List<VehicleAvailabilityDto> updateVehicleAvailabilities(@RequestHeader("Authorization") String authenticationHeaderValue, @RequestBody List<VehicleAvailabilityUpdateDto> vehicleAvailabilityUpdateDtos);
}
