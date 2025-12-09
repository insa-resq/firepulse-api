package org.resq.firepulseapi.accountsservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.jspecify.annotations.NonNull;
import org.resq.firepulseapi.accountsservice.dtos.*;
import org.resq.firepulseapi.accountsservice.entities.enums.UserRole;
import org.resq.firepulseapi.accountsservice.services.TokenService;
import org.resq.firepulseapi.accountsservice.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication Controller", description = "Endpoints for user authentication")
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthenticationController(
            UserService userService,
            AuthenticationManager authenticationManager,
            TokenService tokenService
    ) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @PostMapping("/register/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Register a new admin")
    public ResponseEntity<@NonNull UserDto> registerAdmin(@Valid @RequestBody UserCreationDto userCreationDto) {
        UserDto userDto = userService.createUser(userCreationDto, UserRole.ADMIN);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/register/alert-monitor")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Register a new alert monitor")
    public ResponseEntity<@NonNull UserDto> registerAlertMonitor(@Valid @RequestBody UserCreationDto userCreationDto) {
        UserDto userDto = userService.createUser(userCreationDto, UserRole.ALERT_MONITOR);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/register/planning-manager")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Register a new planning manager")
    public ResponseEntity<@NonNull UserDto> registerPlanningManager(@Valid @RequestBody UserCreationDto userCreationDto) {
        UserDto userDto = userService.createUser(userCreationDto, UserRole.PLANNING_MANAGER);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/register/firefighter")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLANNING_MANAGER')")
    @Operation(summary = "Register a new firefighter")
    public ResponseEntity<@NonNull UserDto> registerFirefighter(@Valid @RequestBody UserCreationDto userCreationDto) {
        UserDto userDto = userService.createUser(userCreationDto, UserRole.FIREFIGHTER);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/login")
    @Operation(summary = "Log in and obtain an authentication token")
    public ResponseEntity<@NonNull TokenDto> login(@Valid @RequestBody LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );

        String token = tokenService.generateToken(authentication);

        TokenDto tokenDto = new TokenDto();
        tokenDto.setToken(token);

        return ResponseEntity.ok(tokenDto);
    }
}
