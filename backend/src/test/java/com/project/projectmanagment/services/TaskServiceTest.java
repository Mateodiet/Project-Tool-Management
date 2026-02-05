package com.project.projectmanagment.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.sql.Date;
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

import com.project.projectmanagment.entities.project.ProjectEntity;
import com.project.projectmanagment.entities.task.TaskEntity;
import com.project.projectmanagment.entities.task.TaskHistory;
import com.project.projectmanagment.entities.user.UserEntity;
import com.project.projectmanagment.models.response.ApiResponse;
import com.project.projectmanagment.models.task.CreateTaskRequest;
import com.project.projectmanagment.repositories.project.ProjectRepository;
import com.project.projectmanagment.repositories.task.*;
import com.project.projectmanagment.repositories.user.UserRepository;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private TaskHistoryRepository historyRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    private TaskEntity testTask;
    private ProjectEntity testProject;
    private UserEntity testUser;
    private CreateTaskRequest createRequest;

    @BeforeEach
    void setUp() {
        testUser = UserEntity.builder()
            .userId(1L)
            .name("Test User")
            .email("test@example.com")
            .build();

        testProject = ProjectEntity.builder()
            .projectId(1L)
            .projectName("Test Project")
            .build();

        testTask = TaskEntity.builder()
            .taskId(1L)
            .taskName("Test Task")
            .taskDescription("Description")
            .taskStatus("TODO")
            .taskPriority("MEDIUM")
            .projectId(1L)
            .assignedTo(1L)
            .createdBy(1L)
            .createdAt(new Date(System.currentTimeMillis()))
            .build();

        createRequest = CreateTaskRequest.builder()
            .taskName("Test Task")
            .taskDescription("Description")
            .taskStatus("TODO")
            .taskPriority("MEDIUM")
            .projectId(1L)
            .assignedTo(1L)
            .createdBy(1L)
            .build();
    }

    @Test
    void createTask_Success() {
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(testProject));
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(testTask);
        when(historyRepository.save(any(TaskHistory.class))).thenReturn(new TaskHistory());

        ApiResponse response = taskService.createTask(createRequest);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Task created successfully", response.getMessage());
    }

    @Test
    void createTask_ProjectNotFound() {
        when(projectRepository.findById(anyLong())).thenReturn(Optional.empty());

        ApiResponse response = taskService.createTask(createRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
    }

    @Test
    void getAllTasks_Success() {
        when(taskRepository.findAll()).thenReturn(Arrays.asList(testTask));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(testProject));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));

        ApiResponse response = taskService.getAllTasks();

        assertEquals(HttpStatus.OK, response.getStatus());
        List<?> tasks = (List<?>) response.getData();
        assertEquals(1, tasks.size());
    }

    @Test
    void getTaskById_Success() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(testProject));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));

        ApiResponse response = taskService.getTaskById(1L);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void getTaskById_NotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        ApiResponse response = taskService.getTaskById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
    }

    @Test
    void getTasksByProject_Success() {
        when(taskRepository.findByProjectId(1L)).thenReturn(Arrays.asList(testTask));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(testProject));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));

        ApiResponse response = taskService.getTasksByProject(1L);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void updateTask_Success() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(testTask);
        when(historyRepository.save(any(TaskHistory.class))).thenReturn(new TaskHistory());
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(testProject));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));

        createRequest.setTaskName("Updated Task");
        ApiResponse response = taskService.updateTask(1L, createRequest, 1L);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void deleteTask_Success() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        doNothing().when(taskRepository).delete(any(TaskEntity.class));

        ApiResponse response = taskService.deleteTask(1L);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void getTaskHistory_Success() {
        TaskHistory history = TaskHistory.builder()
            .historyId(1L)
            .taskId(1L)
            .fieldChanged("taskStatus")
            .oldValue("TODO")
            .newValue("IN_PROGRESS")
            .build();

        when(historyRepository.findByTaskIdOrderByChangedAtDesc(1L)).thenReturn(Arrays.asList(history));

        ApiResponse response = taskService.getTaskHistory(1L);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void getDashboardStats_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(taskRepository.findAll()).thenReturn(Arrays.asList(testTask));
        when(projectRepository.count()).thenReturn(5L);
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(testProject));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));

        ApiResponse response = taskService.getDashboardStats("test@example.com");

        assertEquals(HttpStatus.OK, response.getStatus());
    }
}
