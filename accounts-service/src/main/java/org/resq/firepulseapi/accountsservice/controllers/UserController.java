package org.resq.firepulseapi.accountsservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.resq.firepulseapi.accountsservice.annotations.AuthenticatedUserRole;
import org.resq.firepulseapi.accountsservice.dtos.UserDto;
import org.resq.firepulseapi.accountsservice.dtos.UserProfileUpdateDto;
import org.resq.firepulseapi.accountsservice.dtos.UserStationUpdateDto;
import org.resq.firepulseapi.accountsservice.dtos.UsersFilters;
import org.resq.firepulseapi.accountsservice.entities.enums.UserRole;
import org.resq.firepulseapi.accountsservice.exceptions.ApiException;
import org.resq.firepulseapi.accountsservice.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<UserDto> getAuthenticatedUserProfile(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        UserDto userDto = userService.getUserById(userId);
        return ResponseEntity.ok(userDto);
    }

    @PatchMapping("/me")
    @Operation(summary = "Update the authenticated user's profile")
    public ResponseEntity<UserDto> updateAuthenticatedUserProfile(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UserProfileUpdateDto userProfileUpdateDto
    ) {
        String userId = jwt.getSubject();
        UserDto updatedUserDto = userService.updateUserById(userId, userProfileUpdateDto);
        return ResponseEntity.ok(updatedUserDto);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users (admin only)")
    public ResponseEntity<List<UserDto>> getAllUsers(@Valid @ModelAttribute UsersFilters filters) {
        List<UserDto> users = userService.getAllUsers(filters);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get a user by ID")
    public ResponseEntity<UserDto> getUserById(
            @AuthenticationPrincipal Jwt jwt,
            @AuthenticatedUserRole UserRole authenticatedUserRole,
            @PathVariable String userId
    ) {
        String authenticatedUserId = jwt.getSubject();

        if (authenticatedUserRole != null && authenticatedUserRole != UserRole.ADMIN && !authenticatedUserId.equals(userId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You are not authorized to access this user's information");
        }

        UserDto userDto = userService.getUserById(userId);
        return ResponseEntity.ok(userDto);
    }

    @PatchMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a user's profile by admin")
    public ResponseEntity<UserDto> updateUserProfileByAdmin(
            @PathVariable String userId,
            @Valid @RequestBody UserProfileUpdateDto userProfileUpdateDto
    ) {
        UserDto updatedUserDto = userService.updateUserById(userId, userProfileUpdateDto);
        return ResponseEntity.ok(updatedUserDto);
    }

    @PatchMapping("/{userId}/station")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a user's fire station assignment by admin")
    public ResponseEntity<UserDto> updateUserStationAssignment(
            @PathVariable String userId,
            @Valid @RequestBody UserStationUpdateDto userStationUpdateDto
    ) {
        UserDto updatedUserDto = userService.updateUserStationById(userId, userStationUpdateDto);
        return ResponseEntity.ok(updatedUserDto);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a user by admin")
    public ResponseEntity<Void> deleteUserByAdmin(@PathVariable String userId) {
        userService.deleteUserById(userId);
        return ResponseEntity.noContent().build();
    }
}
