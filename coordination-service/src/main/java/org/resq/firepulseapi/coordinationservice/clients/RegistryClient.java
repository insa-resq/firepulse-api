package org.resq.firepulseapi.coordinationservice.clients;

import org.resq.firepulseapi.coordinationservice.configurations.FeignClientConfig;
import org.resq.firepulseapi.coordinationservice.dtos.FireStationDto;
import org.resq.firepulseapi.coordinationservice.dtos.FireStationOverviewDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "registry-service", configuration = FeignClientConfig.class)
public interface RegistryClient {
    @GetMapping("/fire-stations")
    List<FireStationDto> getFireStations(@RequestHeader("Authorization") String authenticationHeaderValue);

    @GetMapping("/fire-stations/{stationId}/overview")
    FireStationOverviewDto getFireStationOverview(@RequestHeader("Authorization") String authenticationHeaderValue, @PathVariable String stationId);
}
