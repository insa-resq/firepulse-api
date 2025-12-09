package org.resq.firepulseapi.accountsservice.services;

import org.resq.firepulseapi.accountsservice.dtos.UserCreationDto;
import org.resq.firepulseapi.accountsservice.dtos.UserProfileDto;
import org.resq.firepulseapi.accountsservice.entities.User;
import org.resq.firepulseapi.accountsservice.entities.enums.UserRole;
import org.resq.firepulseapi.accountsservice.exceptions.ApiException;
import org.resq.firepulseapi.accountsservice.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserProfileDto createUser(UserCreationDto userCreationDto, UserRole role) {
        if (userRepository.existsByEmail(userCreationDto.getEmail())) {
            throw new ApiException(HttpStatus.CONFLICT, "Email is already in use");
        }

        User user = new User();
        user.setEmail(userCreationDto.getEmail());
        user.setPassword(passwordEncoder.encode(userCreationDto.getPassword()));
        user.setRole(role);

        User newUser = userRepository.save(user);

        newUser.setAvatarUrl(generateAvatarUrl(newUser.getId()));

        userRepository.save(newUser);

        return UserProfileDto.fromEntity(newUser);
    }

    public UserProfileDto getUserProfile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        return UserProfileDto.fromEntity(user);
    }

    private String generateAvatarUrl(String userId) {
        return String.format("https://api.dicebear.com/7.x/bottts-neutral/svg?seed=%s", userId);
    }
}
