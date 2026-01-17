package org.resq.firepulseapi.coordinationservice.clients;

import org.resq.firepulseapi.coordinationservice.configurations.FeignClientConfig;
import org.resq.firepulseapi.coordinationservice.dtos.LoginDto;
import org.resq.firepulseapi.coordinationservice.dtos.TokenDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "accounts-service", configuration = FeignClientConfig.class)
public interface AccountsClient {
    @PostMapping("/auth/login")
    TokenDto login(@RequestBody LoginDto loginDto);
}
