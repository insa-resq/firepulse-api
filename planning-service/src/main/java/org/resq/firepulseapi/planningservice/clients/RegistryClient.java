package org.resq.firepulseapi.planningservice.clients;

import org.resq.firepulseapi.planningservice.configurations.FeignClientConfig;
import org.resq.firepulseapi.planningservice.dtos.FireStationDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "registry-service", configuration = FeignClientConfig.class)
public interface RegistryClient {
    @GetMapping("/fire-stations/{stationId}")
    FireStationDto getFireStationById(@PathVariable String stationId);
}
