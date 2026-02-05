package com.project.projectmanagment.services;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.projectmanagment.entities.project.ProjectEntity;
import com.project.projectmanagment.entities.task.TaskEntity;
import com.project.projectmanagment.entities.task.TaskHistory;
import com.project.projectmanagment.entities.user.UserEntity;
import com.project.projectmanagment.models.response.ApiResponse;
import com.project.projectmanagment.models.task.*;
import com.project.projectmanagment.repositories.project.ProjectRepository;
import com.project.projectmanagment.repositories.task.*;
import com.project.projectmanagment.repositories.user.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskHistoryRepository historyRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Transactional
    public ApiResponse createTask(CreateTaskRequest request) {
        Optional<ProjectEntity> projectOpt = projectRepository.findById(request.getProjectId());
        if (projectOpt.isEmpty()) {
            return ApiResponse.notFound("Project not found");
        }

        TaskEntity task = TaskEntity.builder()
            .taskName(request.getTaskName())
            .taskDescription(request.getTaskDescription())
            .taskStatus(request.getTaskStatus() != null ? request.getTaskStatus() : "TODO")
            .taskPriority(request.getTaskPriority() != null ? request.getTaskPriority() : "MEDIUM")
            .dueDate(request.getDueDate())
            .projectId(request.getProjectId())
            .assignedTo(request.getAssignedTo())
            .createdBy(request.getCreatedBy())
            .createdAt(new Date(System.currentTimeMillis()))
            .updatedAt(new Date(System.currentTimeMillis()))
            .build();

        task = taskRepository.save(task);

        recordHistory(task.getTaskId(), "CREATED", null, "Task created", request.getCreatedBy());

        log.info("Task '{}' created in project {}", task.getTaskName(), request.getProjectId());
        return ApiResponse.success("Task created successfully", toDTO(task));
    }

    public ApiResponse getAllTasks() {
        List<TaskDTO> tasks = taskRepository.findAll().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
        return ApiResponse.success(tasks);
    }

    public ApiResponse getTaskById(Long taskId) {
        Optional<TaskEntity> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            return ApiResponse.notFound("Task not found");
        }
        return ApiResponse.success(toDTO(taskOpt.get()));
    }

    public ApiResponse getTasksByProject(Long projectId) {
        List<TaskDTO> tasks = taskRepository.findByProjectId(projectId).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
        return ApiResponse.success(tasks);
    }

    public ApiResponse getTasksByProjectName(String projectName) {
        Optional<ProjectEntity> projectOpt = projectRepository.findByProjectName(projectName);
        if (projectOpt.isEmpty()) {
            return ApiResponse.notFound("Project not found");
        }
        return getTasksByProject(projectOpt.get().getProjectId());
    }

    public ApiResponse getTasksByUser(Long userId) {
        List<TaskDTO> tasks = taskRepository.findByAssignedTo(userId).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
        return ApiResponse.success(tasks);
    }

    public ApiResponse getTasksByStatus(String status) {
        List<TaskDTO> tasks = taskRepository.findByTaskStatus(status.toUpperCase()).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
        return ApiResponse.success(tasks);
    }

    @Transactional
    public ApiResponse updateTask(Long taskId, CreateTaskRequest request, Long updatedBy) {
        Optional<TaskEntity> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            return ApiResponse.notFound("Task not found");
        }

        TaskEntity task = taskOpt.get();

        if (request.getTaskName() != null && !request.getTaskName().equals(task.getTaskName())) {
            recordHistory(taskId, "taskName", task.getTaskName(), request.getTaskName(), updatedBy);
            task.setTaskName(request.getTaskName());
        }

        if (request.getTaskDescription() != null && !request.getTaskDescription().equals(task.getTaskDescription())) {
            recordHistory(taskId, "taskDescription", task.getTaskDescription(), request.getTaskDescription(), updatedBy);
            task.setTaskDescription(request.getTaskDescription());
        }

        if (request.getTaskStatus() != null && !request.getTaskStatus().equals(task.getTaskStatus())) {
            recordHistory(taskId, "taskStatus", task.getTaskStatus(), request.getTaskStatus(), updatedBy);
            task.setTaskStatus(request.getTaskStatus());
        }

        if (request.getTaskPriority() != null && !request.getTaskPriority().equals(task.getTaskPriority())) {
            recordHistory(taskId, "taskPriority", task.getTaskPriority(), request.getTaskPriority(), updatedBy);
            task.setTaskPriority(request.getTaskPriority());
        }

        if (request.getDueDate() != null && !request.getDueDate().equals(task.getDueDate())) {
            recordHistory(taskId, "dueDate", 
                task.getDueDate() != null ? task.getDueDate().toString() : null, 
                request.getDueDate().toString(), updatedBy);
            task.setDueDate(request.getDueDate());
        }

        if (request.getAssignedTo() != null && !request.getAssignedTo().equals(task.getAssignedTo())) {
            recordHistory(taskId, "assignedTo", 
                task.getAssignedTo() != null ? task.getAssignedTo().toString() : null, 
                request.getAssignedTo().toString(), updatedBy);
            task.setAssignedTo(request.getAssignedTo());
            
            // US11: Send email notification when task is assigned
            sendAssignmentNotification(request.getAssignedTo(), task.getTaskName(), task.getProjectId());
        }

        task.setUpdatedAt(new Date(System.currentTimeMillis()));
        taskRepository.save(task);

        log.info("Task {} updated", taskId);
        return ApiResponse.success("Task updated successfully", toDTO(task));
    }

    @Transactional
    public ApiResponse deleteTask(Long taskId) {
        Optional<TaskEntity> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            return ApiResponse.notFound("Task not found");
        }

        taskRepository.delete(taskOpt.get());
        log.info("Task {} deleted", taskId);
        return ApiResponse.success("Task deleted successfully", null);
    }

    public ApiResponse getTaskHistory(Long taskId) {
        List<TaskHistory> history = historyRepository.findByTaskIdOrderByChangedAtDesc(taskId);
        return ApiResponse.success(history);
    }

    public ApiResponse getDashboardStats(String email) {
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ApiResponse.notFound("User not found");
        }

        List<TaskEntity> allTasks = taskRepository.findAll();
        
        long todoCount = allTasks.stream().filter(t -> "TODO".equals(t.getTaskStatus())).count();
        long inProgressCount = allTasks.stream().filter(t -> "IN_PROGRESS".equals(t.getTaskStatus())).count();
        long completedCount = allTasks.stream().filter(t -> "COMPLETED".equals(t.getTaskStatus())).count();
        long totalProjects = projectRepository.count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalProjects", totalProjects);
        stats.put("todoTasks", todoCount);
        stats.put("inProgressTasks", inProgressCount);
        stats.put("completedTasks", completedCount);
        stats.put("totalTasks", allTasks.size());

        Map<String, List<TaskDTO>> tasksByStatus = new HashMap<>();
        tasksByStatus.put("TODO", allTasks.stream()
            .filter(t -> "TODO".equals(t.getTaskStatus()))
            .map(this::toDTO)
            .collect(Collectors.toList()));
        tasksByStatus.put("IN_PROGRESS", allTasks.stream()
            .filter(t -> "IN_PROGRESS".equals(t.getTaskStatus()))
            .map(this::toDTO)
            .collect(Collectors.toList()));
        tasksByStatus.put("COMPLETED", allTasks.stream()
            .filter(t -> "COMPLETED".equals(t.getTaskStatus()))
            .map(this::toDTO)
            .collect(Collectors.toList()));

        stats.put("tasksByStatus", tasksByStatus);

        return ApiResponse.success(stats);
    }

    private void recordHistory(Long taskId, String field, String oldValue, String newValue, Long changedBy) {
        TaskHistory history = TaskHistory.builder()
            .taskId(taskId)
            .fieldChanged(field)
            .oldValue(oldValue)
            .newValue(newValue)
            .changedBy(changedBy)
            .changedAt(new Timestamp(System.currentTimeMillis()))
            .build();
        historyRepository.save(history);
    }

    private void sendAssignmentNotification(Long assignedToUserId, String taskName, Long projectId) {
        try {
            Optional<UserEntity> userOpt = userRepository.findById(assignedToUserId);
            Optional<ProjectEntity> projectOpt = projectRepository.findById(projectId);
            
            if (userOpt.isPresent() && projectOpt.isPresent()) {
                emailService.sendTaskAssignmentNotification(
                    userOpt.get().getEmail(),
                    taskName,
                    projectOpt.get().getProjectName()
                );
            }
        } catch (Exception e) {
            log.error("Failed to send assignment notification: {}", e.getMessage());
        }
    }

    private TaskDTO toDTO(TaskEntity task) {
        String projectName = null;
        String assignedToName = null;

        if (task.getProjectId() != null) {
            projectRepository.findById(task.getProjectId())
                .ifPresent(p -> {});
            Optional<ProjectEntity> proj = projectRepository.findById(task.getProjectId());
            if (proj.isPresent()) {
                projectName = proj.get().getProjectName();
            }
        }

        if (task.getAssignedTo() != null) {
            Optional<UserEntity> user = userRepository.findById(task.getAssignedTo());
            if (user.isPresent()) {
                assignedToName = user.get().getName();
            }
        }

        return TaskDTO.builder()
            .taskId(task.getTaskId())
            .taskName(task.getTaskName())
            .taskDescription(task.getTaskDescription())
            .taskStatus(task.getTaskStatus())
            .taskPriority(task.getTaskPriority())
            .dueDate(task.getDueDate())
            .projectId(task.getProjectId())
            .projectName(projectName)
            .assignedTo(task.getAssignedTo())
            .assignedToName(assignedToName)
            .createdBy(task.getCreatedBy())
            .createdAt(task.getCreatedAt())
            .updatedAt(task.getUpdatedAt())
            .build();
    }
}