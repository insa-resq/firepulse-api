package org.resq.firepulseapi.planningservice.clients;

import org.resq.firepulseapi.planningservice.configurations.FeignClientConfig;
import org.resq.firepulseapi.planningservice.dtos.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "accounts-service", configuration = FeignClientConfig.class)
public interface AccountsClient {
    @GetMapping("/users/{userId}")
    UserDto getUserById(@PathVariable String userId);
}
