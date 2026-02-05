package com.project.projectmanagment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.projectmanagment.models.response.ApiResponse;
import com.project.projectmanagment.models.user.*;
import com.project.projectmanagment.services.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody RegisterRequest request) {
        ApiResponse response = userService.register(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest request) {
        ApiResponse response = userService.login(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllUsers() {
        ApiResponse response = userService.getAllUsers();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable Long userId) {
        ApiResponse response = userService.getUserById(userId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse> getUserByEmail(@PathVariable String email) {
        ApiResponse response = userService.getUserByEmail(email);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse> updateUser(@PathVariable Long userId, @RequestBody RegisterRequest request) {
        ApiResponse response = userService.updateUser(userId, request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long userId) {
        ApiResponse response = userService.deleteUser(userId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/{userId}/deactivate")
    public ResponseEntity<ApiResponse> deactivateUser(@PathVariable Long userId) {
        ApiResponse response = userService.deactivateUser(userId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
