package org.resq.firepulseapi.accountsservice.controllers;

import org.jspecify.annotations.NonNull;
import org.resq.firepulseapi.accountsservice.dtos.UserProfileDto;
import org.resq.firepulseapi.accountsservice.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<@NonNull UserProfileDto> getAuthenticatedUserProfile(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();

        UserProfileDto profile = userService.getUserProfile(userId);

        return ResponseEntity.ok(profile);
    }
}
