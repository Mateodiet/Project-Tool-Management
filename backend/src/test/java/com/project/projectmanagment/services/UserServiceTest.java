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
    private UserEntity inactiveUser;
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

        inactiveUser = UserEntity.builder()
            .userId(2L)
            .name("Inactive User")
            .email("inactive@example.com")
            .password("password123")
            .contactNumber("0987654321")
            .isActive(false)
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
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void register_EmailExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        ApiResponse response = userService.register(registerRequest);

        assertEquals(HttpStatus.CONFLICT, response.getStatus());
        assertEquals("Email already registered", response.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        ApiResponse response = userService.login(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Login successful", response.getMessage());
        assertNotNull(response.getData());
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
    void login_DeactivatedAccount() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(inactiveUser));
        loginRequest.setEmail("inactive@example.com");

        ApiResponse response = userService.login(loginRequest);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatus());
        assertEquals("Account is deactivated", response.getMessage());
    }

    @Test
    void getAllUsers_Success() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser, inactiveUser));

        ApiResponse response = userService.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatus());
        List<?> users = (List<?>) response.getData();
        assertEquals(2, users.size());
    }

    @Test
    void getAllUsers_EmptyList() {
        when(userRepository.findAll()).thenReturn(Arrays.asList());

        ApiResponse response = userService.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatus());
        List<?> users = (List<?>) response.getData();
        assertEquals(0, users.size());
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
    void getUserByEmail_Success() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        ApiResponse response = userService.getUserByEmail("test@example.com");

        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.getData());
    }

    @Test
    void getUserByEmail_NotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        ApiResponse response = userService.getUserByEmail("unknown@example.com");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("User not found", response.getMessage());
    }

    @Test
    void updateUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(UserEntity.class))).thenReturn(testUser);

        ApiResponse response = userService.updateUser(1L, registerRequest);

        assertEquals(HttpStatus.OK, response.getStatus());
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void updateUser_NotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        ApiResponse response = userService.updateUser(999L, registerRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
    }

    @Test
    void updateUser_PartialUpdate() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(UserEntity.class))).thenReturn(testUser);

        RegisterRequest partialRequest = RegisterRequest.builder()
            .name("New Name")
            .password("")
            .build();

        ApiResponse response = userService.updateUser(1L, partialRequest);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).delete(any(UserEntity.class));

        ApiResponse response = userService.deleteUser(1L);

        assertEquals(HttpStatus.OK, response.getStatus());
        verify(userRepository).delete(testUser);
    }

    @Test
    void deleteUser_NotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        ApiResponse response = userService.deleteUser(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
    }

    @Test
    void deactivateUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(UserEntity.class))).thenReturn(testUser);

        ApiResponse response = userService.deactivateUser(1L);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertFalse(testUser.getIsActive());
    }

    @Test
    void deactivateUser_NotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        ApiResponse response = userService.deactivateUser(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
    }
}
