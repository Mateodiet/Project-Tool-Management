package com.project.projectmanagment.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.project.projectmanagment.models.response.ApiResponse;
import com.project.projectmanagment.models.user.*;
import com.project.projectmanagment.services.UserService;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private ApiResponse okResponse;
    private ApiResponse notFoundResponse;

    @BeforeEach
    void setUp() {
        okResponse = ApiResponse.builder().status(HttpStatus.OK).message("Success").build();
        notFoundResponse = ApiResponse.builder().status(HttpStatus.NOT_FOUND).message("Not found").build();
    }

    @Test
    void register_ShouldReturn200() {
        when(userService.register(any())).thenReturn(okResponse);

        RegisterRequest request = RegisterRequest.builder()
            .name("Test").email("test@test.com").password("pass").build();
        ResponseEntity<ApiResponse> result = userController.register(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(userService).register(any());
    }

    @Test
    void login_ShouldReturn200() {
        when(userService.login(any())).thenReturn(okResponse);

        LoginRequest request = LoginRequest.builder()
            .email("test@test.com").password("pass").build();
        ResponseEntity<ApiResponse> result = userController.login(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void getAllUsers_ShouldReturn200() {
        when(userService.getAllUsers()).thenReturn(okResponse);

        ResponseEntity<ApiResponse> result = userController.getAllUsers();

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void getUserById_ShouldReturn200() {
        when(userService.getUserById(1L)).thenReturn(okResponse);

        ResponseEntity<ApiResponse> result = userController.getUserById(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void getUserById_NotFound() {
        when(userService.getUserById(999L)).thenReturn(notFoundResponse);

        ResponseEntity<ApiResponse> result = userController.getUserById(999L);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    void getUserByEmail_ShouldReturn200() {
        when(userService.getUserByEmail("test@test.com")).thenReturn(okResponse);

        ResponseEntity<ApiResponse> result = userController.getUserByEmail("test@test.com");

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void updateUser_ShouldReturn200() {
        when(userService.updateUser(anyLong(), any())).thenReturn(okResponse);

        RegisterRequest request = RegisterRequest.builder()
            .name("Updated").email("test@test.com").password("pass").build();
        ResponseEntity<ApiResponse> result = userController.updateUser(1L, request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void deleteUser_ShouldReturn200() {
        when(userService.deleteUser(1L)).thenReturn(okResponse);

        ResponseEntity<ApiResponse> result = userController.deleteUser(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void deactivateUser_ShouldReturn200() {
        when(userService.deactivateUser(1L)).thenReturn(okResponse);

        ResponseEntity<ApiResponse> result = userController.deactivateUser(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }
}
