package org.resq.firepulseapi.coordinationservice.clients;

import org.resq.firepulseapi.coordinationservice.configurations.FeignClientConfig;
import org.resq.firepulseapi.coordinationservice.dtos.FireStationDto;
import org.resq.firepulseapi.coordinationservice.dtos.VehicleDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "registry-service", configuration = FeignClientConfig.class)
public interface RegistryClient {
    @GetMapping("/fire-stations")
    List<FireStationDto> getFireStations(@RequestHeader("Authorization") String authenticationHeaderValue);

    @GetMapping("/fire-stations/{stationId}")
    FireStationDto getFireStationById(@RequestHeader("Authorization") String authenticationHeaderValue, @PathVariable String stationId);

    @GetMapping("/vehicles")
    List<VehicleDto> getVehicles(@RequestHeader("Authorization") String authenticationHeaderValue, @RequestParam String stationId);
}
