package org.resq.firepulseapi.coordinationservice.services;

import feign.FeignException;
import org.resq.firepulseapi.coordinationservice.clients.AccountsClient;
import org.resq.firepulseapi.coordinationservice.clients.RegistryClient;
import org.resq.firepulseapi.coordinationservice.dtos.FireStationDto;
import org.resq.firepulseapi.coordinationservice.dtos.FireStationOverviewDto;
import org.resq.firepulseapi.coordinationservice.dtos.LoginDto;
import org.resq.firepulseapi.coordinationservice.dtos.TokenDto;
import org.resq.firepulseapi.coordinationservice.exceptions.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Callable;

@Service
public class CoordinationService {
    private static final Logger logger = LoggerFactory.getLogger(CoordinationService.class);
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
        return executeWithAuthentication(() -> registryClient.getFireStations(authenticationHeaderValue));
    }

    public FireStationOverviewDto getFireStationOverview(String stationId) {
        return executeWithAuthentication(() -> {
            try {
                return registryClient.getFireStationOverview(authenticationHeaderValue, stationId);
            } catch (FeignException.NotFound e) {
                throw new ApiException(HttpStatus.NOT_FOUND, "Fire station not found");
            }
        });
    }

    private <T> T executeWithAuthentication(Callable<T> action) throws ApiException {
        if (authenticationHeaderValue == null) {
            login();
        }
        try {
            return action.call();
        } catch (FeignException.Unauthorized e1) {
            login();
            try {
                return action.call();
            } catch (ApiException e2) {
                throw e2;
            } catch (Exception e3) {
                logger.error("Error executing action after re-authentication", e3);
                throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to contact remote service");
            }
        } catch (ApiException e4) {
            throw e4;
        } catch (Exception e5) {
            logger.error("Error executing action", e5);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to contact remote service");
        }
    }

    private void login() {
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail(adminEmail);
        loginDto.setPassword(adminPassword);
        TokenDto tokenDto = accountsClient.login(loginDto);
        authenticationHeaderValue = "Bearer " + tokenDto.getToken();
    }
}
