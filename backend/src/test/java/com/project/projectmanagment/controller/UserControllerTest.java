package com.project.projectmanagment.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.project.projectmanagment.config.SecurityConfig;
import com.project.projectmanagment.models.response.ApiResponse;
import com.project.projectmanagment.services.UserService;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void register_ShouldReturn200() throws Exception {
        ApiResponse response = ApiResponse.builder()
            .status(HttpStatus.OK)
            .message("User registered successfully")
            .build();

        when(userService.register(any())).thenReturn(response);

        mockMvc.perform(post("/api/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Test\",\"email\":\"test@test.com\",\"password\":\"pass123\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("User registered successfully"));
    }

    @Test
    void login_ShouldReturn200() throws Exception {
        ApiResponse response = ApiResponse.builder()
            .status(HttpStatus.OK)
            .message("Login successful")
            .build();

        when(userService.login(any())).thenReturn(response);

        mockMvc.perform(post("/api/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@test.com\",\"password\":\"pass123\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Login successful"));
    }

    @Test
    void getAllUsers_ShouldReturn200() throws Exception {
        ApiResponse response = ApiResponse.builder()
            .status(HttpStatus.OK)
            .message("Users retrieved")
            .build();

        when(userService.getAllUsers()).thenReturn(response);

        mockMvc.perform(get("/api/user/all"))
            .andExpect(status().isOk());
    }

    @Test
    void getUserById_ShouldReturn200() throws Exception {
        ApiResponse response = ApiResponse.builder()
            .status(HttpStatus.OK)
            .message("User found")
            .build();

        when(userService.getUserById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/user/1"))
            .andExpect(status().isOk());
    }

    @Test
    void getUserById_NotFound() throws Exception {
        ApiResponse response = ApiResponse.builder()
            .status(HttpStatus.NOT_FOUND)
            .message("User not found")
            .build();

        when(userService.getUserById(999L)).thenReturn(response);

        mockMvc.perform(get("/api/user/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void updateUser_ShouldReturn200() throws Exception {
        ApiResponse response = ApiResponse.builder()
            .status(HttpStatus.OK)
            .message("User updated")
            .build();

        when(userService.updateUser(anyLong(), any())).thenReturn(response);

        mockMvc.perform(put("/api/user/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Updated\",\"email\":\"test@test.com\",\"password\":\"pass123\"}"))
            .andExpect(status().isOk());
    }

    @Test
    void deleteUser_ShouldReturn200() throws Exception {
        ApiResponse response = ApiResponse.builder()
            .status(HttpStatus.OK)
            .message("User deleted")
            .build();

        when(userService.deleteUser(1L)).thenReturn(response);

        mockMvc.perform(delete("/api/user/1"))
            .andExpect(status().isOk());
    }

    @Test
    void deactivateUser_ShouldReturn200() throws Exception {
        ApiResponse response = ApiResponse.builder()
            .status(HttpStatus.OK)
            .message("User deactivated")
            .build();

        when(userService.deactivateUser(1L)).thenReturn(response);

        mockMvc.perform(put("/api/user/1/deactivate"))
            .andExpect(status().isOk());
    }
}