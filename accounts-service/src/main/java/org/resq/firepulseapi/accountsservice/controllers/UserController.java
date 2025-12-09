package org.resq.firepulseapi.accountsservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.jspecify.annotations.NonNull;
import org.resq.firepulseapi.accountsservice.dtos.UserDto;
import org.resq.firepulseapi.accountsservice.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@Tag(name = "User Controller", description = "Endpoints for user accounts management")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    @Operation(summary = "Get the authenticated user's profile")
    public ResponseEntity<@NonNull UserDto> getAuthenticatedUserProfile(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();

        UserDto profile = userService.getUserById(userId);

        return ResponseEntity.ok(profile);
    }
}
