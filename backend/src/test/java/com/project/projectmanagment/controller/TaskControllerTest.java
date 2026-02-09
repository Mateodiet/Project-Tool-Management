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
import com.project.projectmanagment.services.TaskService;

@WebMvcTest(TaskController.class)
@Import(SecurityConfig.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Test
    void createTask_ShouldReturn200() throws Exception {
        ApiResponse response = ApiResponse.builder()
            .status(HttpStatus.OK)
            .message("Task created successfully")
            .build();

        when(taskService.createTask(any())).thenReturn(response);

        mockMvc.perform(post("/api/task/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"taskName\":\"Test Task\",\"taskDescription\":\"Desc\",\"taskStatus\":\"TODO\",\"taskPriority\":\"MEDIUM\",\"projectId\":1,\"assignedTo\":1,\"createdBy\":1}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Task created successfully"));
    }

    @Test
    void getAllTasks_ShouldReturn200() throws Exception {
        ApiResponse response = ApiResponse.builder()
            .status(HttpStatus.OK)
            .message("Tasks retrieved")
            .build();

        when(taskService.getAllTasks()).thenReturn(response);

        mockMvc.perform(get("/api/task/all"))
            .andExpect(status().isOk());
    }

    @Test
    void getTaskById_ShouldReturn200() throws Exception {
        ApiResponse response = ApiResponse.builder()
            .status(HttpStatus.OK)
            .message("Task found")
            .build();

        when(taskService.getTaskById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/task/1"))
            .andExpect(status().isOk());
    }

    @Test
    void getTaskById_NotFound() throws Exception {
        ApiResponse response = ApiResponse.builder()
            .status(HttpStatus.NOT_FOUND)
            .message("Task not found")
            .build();

        when(taskService.getTaskById(999L)).thenReturn(response);

        mockMvc.perform(get("/api/task/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void getTasksByProject_ShouldReturn200() throws Exception {
        ApiResponse response = ApiResponse.builder()
            .status(HttpStatus.OK)
            .message("Tasks retrieved")
            .build();

        when(taskService.getTasksByProject(1L)).thenReturn(response);

        mockMvc.perform(get("/api/task/project/1"))
            .andExpect(status().isOk());
    }

    @Test
    void getTasksByProjectName_ShouldReturn200() throws Exception {
        ApiResponse response = ApiResponse.builder()
            .status(HttpStatus.OK)
            .message("Tasks retrieved")
            .build();

        when(taskService.getTasksByProjectName(anyString())).thenReturn(response);

        mockMvc.perform(get("/api/task/project/name/TestProject"))
            .andExpect(status().isOk());
    }

    @Test
    void getTasksByUser_ShouldReturn200() throws Exception {
        ApiResponse response = ApiResponse.builder()
            .status(HttpStatus.OK)
            .message("Tasks retrieved")
            .build();

        when(taskService.getTasksByUser(1L)).thenReturn(response);

        mockMvc.perform(get("/api/task/user/1"))
            .andExpect(status().isOk());
    }

    @Test
    void getTasksByStatus_ShouldReturn200() throws Exception {
        ApiResponse response = ApiResponse.builder()
            .status(HttpStatus.OK)
            .message("Tasks retrieved")
            .build();

        when(taskService.getTasksByStatus(anyString())).thenReturn(response);

        mockMvc.perform(get("/api/task/status/TODO"))
            .andExpect(status().isOk());
    }

    @Test
    void updateTask_ShouldReturn200() throws Exception {
        ApiResponse response = ApiResponse.builder()
            .status(HttpStatus.OK)
            .message("Task updated")
            .build();

        when(taskService.updateTask(anyLong(), any(), anyLong())).thenReturn(response);

        mockMvc.perform(put("/api/task/1")
                .param("updatedBy", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"taskName\":\"Updated\",\"taskDescription\":\"Desc\",\"taskStatus\":\"IN_PROGRESS\",\"taskPriority\":\"HIGH\",\"projectId\":1,\"assignedTo\":1,\"createdBy\":1}"))
            .andExpect(status().isOk());
    }

    @Test
    void deleteTask_ShouldReturn200() throws Exception {
        ApiResponse response = ApiResponse.builder()
            .status(HttpStatus.OK)
            .message("Task deleted")
            .build();

        when(taskService.deleteTask(1L)).thenReturn(response);

        mockMvc.perform(delete("/api/task/1"))
            .andExpect(status().isOk());
    }

    @Test
    void getTaskHistory_ShouldReturn200() throws Exception {
        ApiResponse response = ApiResponse.builder()
            .status(HttpStatus.OK)
            .message("History retrieved")
            .build();

        when(taskService.getTaskHistory(1L)).thenReturn(response);

        mockMvc.perform(get("/api/task/1/history"))
            .andExpect(status().isOk());
    }

    @Test
    void getDashboardStats_ShouldReturn200() throws Exception {
        ApiResponse response = ApiResponse.builder()
            .status(HttpStatus.OK)
            .message("Dashboard stats")
            .build();

        when(taskService.getDashboardStats(anyString())).thenReturn(response);

        mockMvc.perform(get("/api/task/dashboard/test@test.com"))
            .andExpect(status().isOk());
    }
}