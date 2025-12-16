package org.resq.firepulseapi.accountsservice.services;

import feign.FeignException;
import org.resq.firepulseapi.accountsservice.clients.RegistryClient;
import org.resq.firepulseapi.accountsservice.dtos.UserCreationDto;
import org.resq.firepulseapi.accountsservice.dtos.UserDto;
import org.resq.firepulseapi.accountsservice.dtos.UserProfileUpdateDto;
import org.resq.firepulseapi.accountsservice.dtos.UserStationUpdateDto;
import org.resq.firepulseapi.accountsservice.entities.User;
import org.resq.firepulseapi.accountsservice.entities.enums.UserRole;
import org.resq.firepulseapi.accountsservice.exceptions.ApiException;
import org.resq.firepulseapi.accountsservice.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RegistryClient registryClient;

    private static final String DEFAULT_AVATAR_URL = "https://api.dicebear.com/9.x/bottts-neutral/svg";

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RegistryClient registryClient) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.registryClient = registryClient;
    }

    public UserDto createUser(UserCreationDto userCreationDto, UserRole role) {
        if (userRepository.existsByEmail(userCreationDto.getEmail())) {
            throw new ApiException(HttpStatus.CONFLICT, "Email is already in use");
        }

        try {
            registryClient.getFireStationById(userCreationDto.getStationId());
        } catch (FeignException.NotFound e) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Fire station not found");
        }

        User user = new User();
        user.setEmail(userCreationDto.getEmail());
        user.setPassword(passwordEncoder.encode(userCreationDto.getPassword()));
        user.setRole(role);
        user.setStationId(userCreationDto.getStationId());
        user.setAvatarUrl(DEFAULT_AVATAR_URL);

        User newUser = userRepository.save(user);

        newUser.setAvatarUrl(generateAvatarUrl(newUser.getId()));

        userRepository.save(newUser);

        return UserDto.fromEntity(newUser);
    }

    public UserDto getUserById(String userId) {
        return userRepository.findById(userId)
                .map(UserDto::fromEntity)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public UserDto updateUserById(String userId, UserProfileUpdateDto userProfileUpdateDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        User updatedUser = user;

        if (userProfileUpdateDto.getEmail() != null && !userProfileUpdateDto.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(userProfileUpdateDto.getEmail())) {
                throw new ApiException(HttpStatus.CONFLICT, "Email is already in use");
            }

            user.setEmail(userProfileUpdateDto.getEmail());

            updatedUser = userRepository.save(user);
        }

        return UserDto.fromEntity(updatedUser);
    }

    public UserDto updateUserStationById(String userId, UserStationUpdateDto userStationUpdateDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        try {
            registryClient.getFireStationById(userStationUpdateDto.getStationId());
        } catch (FeignException.NotFound e) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Fire station not found");
        }

        user.setStationId(userStationUpdateDto.getStationId());

        User updatedUser = userRepository.save(user);

        return UserDto.fromEntity(updatedUser);
    }

    @Transactional
    public void deleteUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        userRepository.delete(user);
    }

    private String generateAvatarUrl(String userId) {
        return String.format("https://api.dicebear.com/9.x/bottts-neutral/svg?seed=%s", userId);
    }
}
