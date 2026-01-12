package org.resq.firepulseapi.coordinationservice.clients;

import org.resq.firepulseapi.coordinationservice.configurations.FeignClientConfig;
import org.resq.firepulseapi.coordinationservice.dtos.FireStationDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "registry-service", configuration = FeignClientConfig.class)
public interface RegistryClient {
    @GetMapping("/fire-stations")
    List<FireStationDto> getFireStationById();
}
