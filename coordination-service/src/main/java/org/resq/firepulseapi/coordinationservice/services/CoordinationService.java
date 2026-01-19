package org.resq.firepulseapi.coordinationservice.services;

import feign.FeignException;
import org.resq.firepulseapi.coordinationservice.clients.AccountsClient;
import org.resq.firepulseapi.coordinationservice.clients.PlanningClient;
import org.resq.firepulseapi.coordinationservice.clients.RegistryClient;
import org.resq.firepulseapi.coordinationservice.dtos.*;
import org.resq.firepulseapi.coordinationservice.entities.enums.VehicleType;
import org.resq.firepulseapi.coordinationservice.entities.enums.Weekday;
import org.resq.firepulseapi.coordinationservice.exceptions.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@Service
public class CoordinationService {
    private static final Logger logger = LoggerFactory.getLogger(CoordinationService.class);
    private final AccountsClient accountsClient;
    private final RegistryClient registryClient;
    private final PlanningClient planningClient;
    private static String authenticationHeaderValue;

    @Value("${http.internal.admin-email}")
    private String adminEmail;

    @Value("${http.internal.admin-password}")
    private String adminPassword;

    public CoordinationService(AccountsClient accountsClient, RegistryClient registryClient, PlanningClient planningClient) {
        this.accountsClient = accountsClient;
        this.registryClient = registryClient;
        this.planningClient = planningClient;
    }

    public List<FireStationDto> getAllFireStations() {
        return executeWithAuthentication(() -> registryClient.getFireStations(authenticationHeaderValue));
    }

    public FireStationOverviewDto getFireStationOverview(String stationId) {
        return executeWithAuthentication(() -> {
            try {
                registryClient.getFireStationById(authenticationHeaderValue, stationId);
            } catch (FeignException.NotFound e) {
                throw new ApiException(HttpStatus.NOT_FOUND, "Fire station not found");
            }

            Map<String, VehicleDto> vehiclesMap = registryClient.getVehicles(authenticationHeaderValue, stationId)
                    .stream()
                    .collect(Collectors.toMap(VehicleDto::getId, vehicleDto -> vehicleDto));

            Weekday currentWeekday = getCurrentWeekday();

            List<VehicleAvailabilityDto> vehicleAvailabilitiesMap = planningClient.getVehicleAvailabilities(
                    authenticationHeaderValue,
                    vehiclesMap.keySet().stream().toList(),
                    currentWeekday
            );

            List<FireStationOverviewDto.AvailableVehicleDto> availableVehicleDtos =
                    vehicleAvailabilitiesMap.stream()
                            .map(vaDto -> {
                                FireStationOverviewDto.AvailableVehicleDto availableVehicleDto = new FireStationOverviewDto.AvailableVehicleDto();
                                availableVehicleDto.setType(vehiclesMap.get(vaDto.getVehicleId()).getType());
                                availableVehicleDto.setCount(vaDto.getAvailableCount() - vaDto.getBookedCount());
                                return availableVehicleDto;
                            })
                            .toList();

            return new FireStationOverviewDto(availableVehicleDtos);
        });
    }

    public void bookVehicles(List<FireStationBookingDto> fireStationBookingDtos) {
        executeWithAuthentication(() -> {
            Weekday currentWeekday = getCurrentWeekday();

            fireStationBookingDtos.forEach(dto -> {
                Map<VehicleType, VehicleDto> fireStationVehiclesMap =
                        registryClient.getVehicles(authenticationHeaderValue, dto.getStationId())
                                .stream()
                                .collect(Collectors.toMap(VehicleDto::getType, vehicleDto -> vehicleDto));

                Map<String, VehicleAvailabilityDto> vehicleAvailabilitiesMap =
                        planningClient.getVehicleAvailabilities(
                                authenticationHeaderValue,
                                fireStationVehiclesMap.values().stream().map(VehicleDto::getId).toList(),
                                currentWeekday
                        ).stream()
                                .collect(Collectors.toMap(VehicleAvailabilityDto::getVehicleId, vaDto -> vaDto));

                List<VehicleAvailabilityUpdateDto> vehicleAvailabilityUpdateDtos = dto.getVehicles()
                        .stream()
                        .map(bookingDto -> {
                            VehicleDto vehicleDto = fireStationVehiclesMap.get(bookingDto.getType());

                            if (vehicleDto == null) {
                                throw new ApiException(HttpStatus.NOT_FOUND, "Vehicle type " + bookingDto.getType() + " not found at station " + dto.getStationId());
                            }

                            VehicleAvailabilityDto vehicleAvailabilityDto = vehicleAvailabilitiesMap.get(vehicleDto.getId());

                            if (vehicleAvailabilityDto.getAvailableCount() < vehicleAvailabilityDto.getBookedCount() + bookingDto.getBookedCount()) {
                                throw new ApiException(HttpStatus.BAD_REQUEST, "Not enough vehicles of type " + bookingDto.getType() + " available at station " + dto.getStationId());
                            }

                            VehicleAvailabilityUpdateDto updateDto = new VehicleAvailabilityUpdateDto();
                            updateDto.setAvailabilityId(vehicleAvailabilityDto.getId());
                            updateDto.setBookedCount(vehicleAvailabilityDto.getBookedCount() + bookingDto.getBookedCount());
                            return updateDto;
                        })
                        .toList();

                planningClient.updateVehicleAvailabilities(authenticationHeaderValue, vehicleAvailabilityUpdateDtos);
            });

            return null;
        });
    }

    public void dropVehicles(List<FireStationDroppingDto> fireStationDroppingDtos) {
        executeWithAuthentication(() -> {
            Weekday currentWeekday = getCurrentWeekday();

            fireStationDroppingDtos.forEach(dto -> {
                Map<VehicleType, VehicleDto> fireStationVehiclesMap =
                        registryClient.getVehicles(authenticationHeaderValue, dto.getStationId())
                                .stream()
                                .collect(Collectors.toMap(VehicleDto::getType, vehicleDto -> vehicleDto));

                Map<String, VehicleAvailabilityDto> vehicleAvailabilitiesMap =
                        planningClient.getVehicleAvailabilities(
                                authenticationHeaderValue,
                                fireStationVehiclesMap.values().stream().map(VehicleDto::getId).toList(),
                                currentWeekday
                        ).stream()
                                .collect(Collectors.toMap(VehicleAvailabilityDto::getVehicleId, vaDto -> vaDto));

                List<VehicleAvailabilityUpdateDto> vehicleAvailabilityUpdateDtos = dto.getVehicles()
                        .stream()
                        .map(droppingDto -> {
                            VehicleDto vehicleDto = fireStationVehiclesMap.get(droppingDto.getType());

                            if (vehicleDto == null) {
                                throw new ApiException(HttpStatus.NOT_FOUND, "Vehicle type " + droppingDto.getType() + " not found at station " + dto.getStationId());
                            }

                            VehicleAvailabilityDto vehicleAvailabilityDto = vehicleAvailabilitiesMap.get(vehicleDto.getId());

                            if (vehicleAvailabilityDto.getBookedCount() < droppingDto.getDroppedCount()) {
                                throw new ApiException(HttpStatus.BAD_REQUEST, "Cannot drop more vehicles of type " + droppingDto.getType() + " than are booked at station " + dto.getStationId());
                            }

                            VehicleAvailabilityUpdateDto updateDto = new VehicleAvailabilityUpdateDto();
                            updateDto.setAvailabilityId(vehicleAvailabilityDto.getId());
                            updateDto.setBookedCount(vehicleAvailabilityDto.getBookedCount() - droppingDto.getDroppedCount());
                            return updateDto;
                        })
                        .toList();

                planningClient.updateVehicleAvailabilities(authenticationHeaderValue, vehicleAvailabilityUpdateDtos);
            });

            return null;
        });
    }

    private Weekday getCurrentWeekday() {
        return Weekday.values()[LocalDate.now().getDayOfWeek().getValue() - 1];
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
