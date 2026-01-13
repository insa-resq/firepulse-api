package org.resq.firepulseapi.coordinationservice.services;

import org.resq.firepulseapi.coordinationservice.clients.AccountsClient;
import org.resq.firepulseapi.coordinationservice.clients.RegistryClient;
import org.resq.firepulseapi.coordinationservice.dtos.FireStationDto;
import org.resq.firepulseapi.coordinationservice.dtos.LoginDto;
import org.resq.firepulseapi.coordinationservice.dtos.TokenDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoordinationService {
    private final AccountsClient accountsClient;
    private final RegistryClient registryClient;
    private static String authenticationHeaderValue;

    @Value("${http.internal.admin-email}")
    private String adminEmail;

    @Value("${http.internal.admin-password}")
    private String adminPassword;

    public CoordinationService(AccountsClient accountsClient, RegistryClient registryClient) {
        this.accountsClient = accountsClient;
        this.registryClient = registryClient;
    }

    public List<FireStationDto> getAllFireStations() {
        ensureAuthenticated();
        return registryClient.getFireStations(authenticationHeaderValue);
    }

    private void ensureAuthenticated() throws RuntimeException {
        if (authenticationHeaderValue == null) {
            LoginDto loginDto = new LoginDto();
            loginDto.setEmail(adminEmail);
            loginDto.setPassword(adminPassword);
            TokenDto tokenDto = accountsClient.login(loginDto);
            authenticationHeaderValue = "Bearer " + tokenDto.getToken();
        }
    }
}
