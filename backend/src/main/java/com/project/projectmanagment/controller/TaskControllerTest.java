package com.project.projectmanagment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.projectmanagment.dto.TaskDTO;
import com.project.projectmanagment.services.TaskService;
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

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    private TaskDTO testTask;

    @BeforeEach
    void setUp() {
        testTask = TaskDTO.builder()
                .taskId(1L)
                .taskName("Test Task")
                .taskDescription("Test Description")
                .taskStatus("TODO")
                .taskPriority("HIGH")
                .projectId(1L)
                .build();
    }

    @Test
    void getTasksByProject_ShouldReturnTasks() throws Exception {
        when(taskService.getTasksByProject("Test Project"))
                .thenReturn(ApiResponse.success(Arrays.asList(testTask)));

        mockMvc.perform(get("/api/tasks/project/Test Project"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"));
    }

    @Test
    void getTaskById_ShouldReturnTask() throws Exception {
        when(taskService.getTaskById(1L))
                .thenReturn(ApiResponse.success(testTask));

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.taskName").value("Test Task"));
    }

    @Test
    void getTaskById_NotFound_ShouldReturn404() throws Exception {
        when(taskService.getTaskById(999L))
                .thenReturn(ApiResponse.notFound("Task not found"));

        mockMvc.perform(get("/api/tasks/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createTask_ShouldReturnCreated() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("taskName", "New Task");
        request.put("taskDescription", "Description");
        request.put("projectName", "Test Project");
        request.put("createdByEmail", "test@test.com");

        when(taskService.createTask(any()))
                .thenReturn(ApiResponse.created("Task created", testTask));

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void updateTask_ShouldReturnOk() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("taskName", "Updated Task");
        request.put("taskStatus", "IN_PROGRESS");

        when(taskService.updateTask(eq(1L), any()))
                .thenReturn(ApiResponse.success("Updated", testTask));

        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteTask_ShouldReturnOk() throws Exception {
        when(taskService.deleteTask(1L))
                .thenReturn(ApiResponse.success("Deleted", null));

        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getTaskHistory_ShouldReturnHistory() throws Exception {
        when(taskService.getTaskHistory(1L))
                .thenReturn(ApiResponse.success(Arrays.asList()));

        mockMvc.perform(get("/api/tasks/1/history"))
                .andExpect(status().isOk());
    }

    @Test
    void getTasksByStatus_ShouldReturnFilteredTasks() throws Exception {
        when(taskService.getTasksByProjectAndStatus("Test Project", "TODO"))
                .thenReturn(ApiResponse.success(Arrays.asList(testTask)));

        mockMvc.perform(get("/api/tasks/project/Test Project/status/TODO"))
                .andExpect(status().isOk());
    }
}