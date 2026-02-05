package com.project.projectmanagment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.projectmanagment.dto.UserDTO;
import com.project.projectmanagment.services.UserService;
import com.project.projectmanagment.utils.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDTO testUser;

    @BeforeEach
    void setUp() {
        testUser = UserDTO.builder()
                .userId(1L)
                .name("Test User")
                .email("test@test.com")
                .isActive(true)
                .build();
    }

    @Test
    void getAllUsers_ShouldReturnUsers() throws Exception {
        when(userService.getAllUsers())
                .thenReturn(ApiResponse.success(Arrays.asList(testUser)));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"));
    }

    @Test
    void getUserByEmail_ShouldReturnUser() throws Exception {
        when(userService.getUserByEmail("test@test.com"))
                .thenReturn(ApiResponse.success(testUser));

        mockMvc.perform(get("/api/users/test@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("test@test.com"));
    }

    @Test
    void getUserByEmail_NotFound_ShouldReturn404() throws Exception {
        when(userService.getUserByEmail("unknown@test.com"))
                .thenReturn(ApiResponse.notFound("User not found"));

        mockMvc.perform(get("/api/users/unknown@test.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    void register_ShouldReturnCreated() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("name", "New User");
        request.put("email", "new@test.com");
        request.put("password", "password123");

        when(userService.register(any()))
                .thenReturn(ApiResponse.created("User registered", testUser));

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void login_ShouldReturnOk() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("email", "test@test.com");
        request.put("password", "password123");

        when(userService.login(anyString(), anyString()))
                .thenReturn(ApiResponse.success("Login successful", testUser));

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void login_InvalidCredentials_ShouldReturn401() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("email", "test@test.com");
        request.put("password", "wrongpassword");

        when(userService.login(anyString(), anyString()))
                .thenReturn(ApiResponse.unauthorized("Invalid credentials"));

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateUser_ShouldReturnOk() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("name", "Updated Name");

        when(userService.updateUser(eq(1L), any()))
                .thenReturn(ApiResponse.success("Updated", testUser));

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}