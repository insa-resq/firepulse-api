package org.resq.firepulseapi.planningservice.clients;

import org.resq.firepulseapi.planningservice.configurations.FeignClientConfig;
import org.resq.firepulseapi.planningservice.dtos.FireStationDto;
import org.resq.firepulseapi.planningservice.dtos.FirefighterDto;
import org.resq.firepulseapi.planningservice.dtos.VehicleDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "registry-service", configuration = FeignClientConfig.class)
public interface RegistryClient {
    @GetMapping("/fire-stations/{stationId}")
    FireStationDto getFireStationById(@PathVariable String stationId);

    @GetMapping("/firefighters")
    List<FirefighterDto> getFirefighters(@RequestParam String stationId);

    @GetMapping("/vehicles")
    List<VehicleDto> getVehicles(@RequestParam String stationId);
}
