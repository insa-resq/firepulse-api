package org.resq.firepulseapi.accountsservice.clients;

import org.resq.firepulseapi.accountsservice.configurations.FeignClientConfig;
import org.resq.firepulseapi.accountsservice.dtos.FireStationDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "registry-service", configuration = FeignClientConfig.class)
public interface RegistryClient {
    @GetMapping("/fire-stations/{stationId}")
    FireStationDto getFireStationById(@PathVariable String stationId);
}
