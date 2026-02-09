package com.project.projectmanagment.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.sql.Date;
import java.util.Arrays;
import java.util.Collections;
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
    @Mock
    private EmailService emailService;

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
    void createTask_WithNullStatusAndPriority() {
        CreateTaskRequest req = CreateTaskRequest.builder()
            .taskName("Task")
            .taskDescription("Desc")
            .taskStatus(null)
            .taskPriority(null)
            .projectId(1L)
            .assignedTo(1L)
            .createdBy(1L)
            .build();

        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(testProject));
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(testTask);
        when(historyRepository.save(any(TaskHistory.class))).thenReturn(new TaskHistory());

        ApiResponse response = taskService.createTask(req);

        assertEquals(HttpStatus.OK, response.getStatus());
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
    void getAllTasks_Empty() {
        when(taskRepository.findAll()).thenReturn(Collections.emptyList());

        ApiResponse response = taskService.getAllTasks();

        assertEquals(HttpStatus.OK, response.getStatus());
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
    void getTasksByProjectName_Success() {
        when(projectRepository.findByProjectName("Test Project")).thenReturn(Optional.of(testProject));
        when(taskRepository.findByProjectId(1L)).thenReturn(Arrays.asList(testTask));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(testProject));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));

        ApiResponse response = taskService.getTasksByProjectName("Test Project");

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void getTasksByProjectName_ProjectNotFound() {
        when(projectRepository.findByProjectName(anyString())).thenReturn(Optional.empty());

        ApiResponse response = taskService.getTasksByProjectName("Unknown");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
    }

    @Test
    void getTasksByUser_Success() {
        when(taskRepository.findByAssignedTo(1L)).thenReturn(Arrays.asList(testTask));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(testProject));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));

        ApiResponse response = taskService.getTasksByUser(1L);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void getTasksByStatus_Success() {
        when(taskRepository.findByTaskStatus("TODO")).thenReturn(Arrays.asList(testTask));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(testProject));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));

        ApiResponse response = taskService.getTasksByStatus("TODO");

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
        createRequest.setTaskStatus("IN_PROGRESS");
        createRequest.setTaskPriority("HIGH");
        createRequest.setTaskDescription("Updated desc");
        ApiResponse response = taskService.updateTask(1L, createRequest, 1L);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void updateTask_NotFound() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        ApiResponse response = taskService.updateTask(999L, createRequest, 1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
    }

    @Test
    void updateTask_WithAssigneeChange() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(testTask);
        when(historyRepository.save(any(TaskHistory.class))).thenReturn(new TaskHistory());
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(testProject));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        doNothing().when(emailService).sendTaskAssignmentNotification(anyString(), anyString(), anyString());

        createRequest.setAssignedTo(2L);
        ApiResponse response = taskService.updateTask(1L, createRequest, 1L);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void updateTask_WithDueDateChange() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(testTask);
        when(historyRepository.save(any(TaskHistory.class))).thenReturn(new TaskHistory());
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(testProject));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));

        createRequest.setDueDate(new Date(System.currentTimeMillis() + 86400000));
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
    void deleteTask_NotFound() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        ApiResponse response = taskService.deleteTask(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
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
    void getTaskHistory_Empty() {
        when(historyRepository.findByTaskIdOrderByChangedAtDesc(1L)).thenReturn(Collections.emptyList());

        ApiResponse response = taskService.getTaskHistory(1L);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void getDashboardStats_Success() {
        TaskEntity todoTask = TaskEntity.builder().taskId(1L).taskStatus("TODO").projectId(1L).assignedTo(1L).build();
        TaskEntity ipTask = TaskEntity.builder().taskId(2L).taskStatus("IN_PROGRESS").projectId(1L).assignedTo(1L).build();
        TaskEntity doneTask = TaskEntity.builder().taskId(3L).taskStatus("COMPLETED").projectId(1L).assignedTo(1L).build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(taskRepository.findAll()).thenReturn(Arrays.asList(todoTask, ipTask, doneTask));
        when(projectRepository.count()).thenReturn(5L);
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(testProject));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));

        ApiResponse response = taskService.getDashboardStats("test@example.com");

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void getDashboardStats_UserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        ApiResponse response = taskService.getDashboardStats("unknown@example.com");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
    }

    @Test
    void toDTO_WithNullProjectAndAssignee() {
        TaskEntity taskNoRefs = TaskEntity.builder()
            .taskId(2L)
            .taskName("Orphan Task")
            .taskDescription("No project or assignee")
            .taskStatus("TODO")
            .taskPriority("LOW")
            .projectId(null)
            .assignedTo(null)
            .createdBy(1L)
            .build();

        when(taskRepository.findAll()).thenReturn(Arrays.asList(taskNoRefs));

        ApiResponse response = taskService.getAllTasks();

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void toDTO_WithMissingProjectInDb() {
        when(taskRepository.findAll()).thenReturn(Arrays.asList(testTask));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        ApiResponse response = taskService.getAllTasks();

        assertEquals(HttpStatus.OK, response.getStatus());
    }
}