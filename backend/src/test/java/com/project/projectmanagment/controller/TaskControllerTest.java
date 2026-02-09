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
import com.project.projectmanagment.models.task.CreateTaskRequest;
import com.project.projectmanagment.services.TaskService;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private ApiResponse okResponse;
    private ApiResponse notFoundResponse;

    @BeforeEach
    void setUp() {
        okResponse = ApiResponse.builder().status(HttpStatus.OK).message("Success").build();
        notFoundResponse = ApiResponse.builder().status(HttpStatus.NOT_FOUND).message("Not found").build();
    }

    @Test
    void createTask_ShouldReturn200() {
        when(taskService.createTask(any())).thenReturn(okResponse);

        CreateTaskRequest request = CreateTaskRequest.builder()
            .taskName("Task").taskDescription("Desc").projectId(1L).build();
        ResponseEntity<ApiResponse> result = taskController.createTask(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void getAllTasks_ShouldReturn200() {
        when(taskService.getAllTasks()).thenReturn(okResponse);

        ResponseEntity<ApiResponse> result = taskController.getAllTasks();

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void getTaskById_ShouldReturn200() {
        when(taskService.getTaskById(1L)).thenReturn(okResponse);

        ResponseEntity<ApiResponse> result = taskController.getTaskById(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void getTaskById_NotFound() {
        when(taskService.getTaskById(999L)).thenReturn(notFoundResponse);

        ResponseEntity<ApiResponse> result = taskController.getTaskById(999L);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    void getTasksByProject_ShouldReturn200() {
        when(taskService.getTasksByProject(1L)).thenReturn(okResponse);

        ResponseEntity<ApiResponse> result = taskController.getTasksByProject(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void getTasksByProjectName_ShouldReturn200() {
        when(taskService.getTasksByProjectName(anyString())).thenReturn(okResponse);

        ResponseEntity<ApiResponse> result = taskController.getTasksByProjectName("Test");

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void getTasksByUser_ShouldReturn200() {
        when(taskService.getTasksByUser(1L)).thenReturn(okResponse);

        ResponseEntity<ApiResponse> result = taskController.getTasksByUser(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void getTasksByStatus_ShouldReturn200() {
        when(taskService.getTasksByStatus(anyString())).thenReturn(okResponse);

        ResponseEntity<ApiResponse> result = taskController.getTasksByStatus("TODO");

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void updateTask_ShouldReturn200() {
        when(taskService.updateTask(anyLong(), any(), anyLong())).thenReturn(okResponse);

        CreateTaskRequest request = CreateTaskRequest.builder()
            .taskName("Updated").taskDescription("Desc").projectId(1L).build();
        ResponseEntity<ApiResponse> result = taskController.updateTask(1L, request, 1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void deleteTask_ShouldReturn200() {
        when(taskService.deleteTask(1L)).thenReturn(okResponse);

        ResponseEntity<ApiResponse> result = taskController.deleteTask(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void getTaskHistory_ShouldReturn200() {
        when(taskService.getTaskHistory(1L)).thenReturn(okResponse);

        ResponseEntity<ApiResponse> result = taskController.getTaskHistory(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void getDashboardStats_ShouldReturn200() {
        when(taskService.getDashboardStats(anyString())).thenReturn(okResponse);

        ResponseEntity<ApiResponse> result = taskController.getDashboardStats("test@test.com");

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }
}