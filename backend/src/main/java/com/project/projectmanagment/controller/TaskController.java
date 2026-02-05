package com.project.projectmanagment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.projectmanagment.models.response.ApiResponse;
import com.project.projectmanagment.models.task.CreateTaskRequest;
import com.project.projectmanagment.services.TaskService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/task")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createTask(@RequestBody CreateTaskRequest request) {
        ApiResponse response = taskService.createTask(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllTasks() {
        ApiResponse response = taskService.getAllTasks();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<ApiResponse> getTaskById(@PathVariable Long taskId) {
        ApiResponse response = taskService.getTaskById(taskId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<ApiResponse> getTasksByProject(@PathVariable Long projectId) {
        ApiResponse response = taskService.getTasksByProject(projectId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/project/name/{projectName}")
    public ResponseEntity<ApiResponse> getTasksByProjectName(@PathVariable String projectName) {
        ApiResponse response = taskService.getTasksByProjectName(projectName);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getTasksByUser(@PathVariable Long userId) {
        ApiResponse response = taskService.getTasksByUser(userId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse> getTasksByStatus(@PathVariable String status) {
        ApiResponse response = taskService.getTasksByStatus(status);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<ApiResponse> updateTask(@PathVariable Long taskId, 
            @RequestBody CreateTaskRequest request, @RequestParam Long updatedBy) {
        ApiResponse response = taskService.updateTask(taskId, request, updatedBy);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<ApiResponse> deleteTask(@PathVariable Long taskId) {
        ApiResponse response = taskService.deleteTask(taskId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/{taskId}/history")
    public ResponseEntity<ApiResponse> getTaskHistory(@PathVariable Long taskId) {
        ApiResponse response = taskService.getTaskHistory(taskId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/dashboard/{email}")
    public ResponseEntity<ApiResponse> getDashboardStats(@PathVariable String email) {
        ApiResponse response = taskService.getDashboardStats(email);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
