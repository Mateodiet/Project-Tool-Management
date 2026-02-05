package com.project.projectmanagment.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.project.projectmanagment.entities.user.UserEntity;
import com.project.projectmanagment.models.response.ApiResponse;
import com.project.projectmanagment.models.user.*;
import com.project.projectmanagment.repositories.user.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private UserEntity testUser;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = UserEntity.builder()
            .userId(1L)
            .name("Test User")
            .email("test@example.com")
            .password("password123")
            .contactNumber("1234567890")
            .isActive(true)
            .build();

        registerRequest = RegisterRequest.builder()
            .name("Test User")
            .email("test@example.com")
            .password("password123")
            .contactNumber("1234567890")
            .build();

        loginRequest = LoginRequest.builder()
            .email("test@example.com")
            .password("password123")
            .build();
    }

    @Test
    void register_Success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenReturn(testUser);

        ApiResponse response = userService.register(registerRequest);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("User registered successfully", response.getMessage());
        assertNotNull(response.getData());
    }

    @Test
    void register_EmailExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        ApiResponse response = userService.register(registerRequest);

        assertEquals(HttpStatus.CONFLICT, response.getStatus());
        assertEquals("Email already registered", response.getMessage());
    }

    @Test
    void login_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        ApiResponse response = userService.login(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Login successful", response.getMessage());
    }

    @Test
    void login_UserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        ApiResponse response = userService.login(loginRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("User not found", response.getMessage());
    }

    @Test
    void login_InvalidPassword() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        loginRequest.setPassword("wrongpassword");

        ApiResponse response = userService.login(loginRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatus());
    }

    @Test
    void getAllUsers_Success() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser));

        ApiResponse response = userService.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatus());
        List<?> users = (List<?>) response.getData();
        assertEquals(1, users.size());
    }

    @Test
    void getUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        ApiResponse response = userService.getUserById(1L);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.getData());
    }

    @Test
    void getUserById_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ApiResponse response = userService.getUserById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
    }

    @Test
    void updateUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(UserEntity.class))).thenReturn(testUser);

        ApiResponse response = userService.updateUser(1L, registerRequest);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).delete(any(UserEntity.class));

        ApiResponse response = userService.deleteUser(1L);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void deactivateUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(UserEntity.class))).thenReturn(testUser);

        ApiResponse response = userService.deactivateUser(1L);

        assertEquals(HttpStatus.OK, response.getStatus());
    }
}
