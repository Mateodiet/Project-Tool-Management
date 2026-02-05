package com.project.projectmanagment.services;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.project.projectmanagment.entities.user.UserEntity;
import com.project.projectmanagment.models.response.ApiResponse;
import com.project.projectmanagment.models.user.*;
import com.project.projectmanagment.repositories.user.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public ApiResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ApiResponse.conflict("Email already registered");
        }

        UserEntity user = UserEntity.builder()
            .name(request.getName())
            .email(request.getEmail())
            .password(request.getPassword())
            .contactNumber(request.getContactNumber())
            .createdAt(new Date(System.currentTimeMillis()))
            .isActive(true)
            .build();

        userRepository.save(user);
        log.info("User registered: {}", request.getEmail());

        return ApiResponse.success("User registered successfully", toDTO(user));
    }

    public ApiResponse login(LoginRequest request) {
        Optional<UserEntity> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            return ApiResponse.notFound("User not found");
        }

        UserEntity user = userOpt.get();
        if (!user.getPassword().equals(request.getPassword())) {
            return ApiResponse.unauthorized("Invalid password");
        }

        if (!user.getIsActive()) {
            return ApiResponse.forbidden("Account is deactivated");
        }

        log.info("User logged in: {}", request.getEmail());
        return ApiResponse.success("Login successful", toDTO(user));
    }

    public ApiResponse getAllUsers() {
        List<UserDTO> users = userRepository.findAll().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
        return ApiResponse.success(users);
    }

    public ApiResponse getUserById(Long userId) {
        Optional<UserEntity> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ApiResponse.notFound("User not found");
        }
        return ApiResponse.success(toDTO(userOpt.get()));
    }

    public ApiResponse getUserByEmail(String email) {
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ApiResponse.notFound("User not found");
        }
        return ApiResponse.success(toDTO(userOpt.get()));
    }

    public ApiResponse updateUser(Long userId, RegisterRequest request) {
        Optional<UserEntity> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ApiResponse.notFound("User not found");
        }

        UserEntity user = userOpt.get();
        if (request.getName() != null) user.setName(request.getName());
        if (request.getContactNumber() != null) user.setContactNumber(request.getContactNumber());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(request.getPassword());
        }

        userRepository.save(user);
        log.info("User updated: {}", user.getEmail());
        return ApiResponse.success("User updated successfully", toDTO(user));
    }

    public ApiResponse deleteUser(Long userId) {
        Optional<UserEntity> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ApiResponse.notFound("User not found");
        }

        userRepository.delete(userOpt.get());
        log.info("User deleted: {}", userId);
        return ApiResponse.success("User deleted successfully", null);
    }

    public ApiResponse deactivateUser(Long userId) {
        Optional<UserEntity> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ApiResponse.notFound("User not found");
        }

        UserEntity user = userOpt.get();
        user.setIsActive(false);
        userRepository.save(user);
        log.info("User deactivated: {}", userId);
        return ApiResponse.success("User deactivated successfully", toDTO(user));
    }

    private UserDTO toDTO(UserEntity user) {
        return UserDTO.builder()
            .userId(user.getUserId())
            .name(user.getName())
            .email(user.getEmail())
            .contactNumber(user.getContactNumber())
            .isActive(user.getIsActive())
            .build();
    }
}
