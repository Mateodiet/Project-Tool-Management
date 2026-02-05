package com.project.projectmanagment.services;

import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.projectmanagment.entities.bridges.ProjectMember;
import com.project.projectmanagment.entities.project.ProjectEntity;
import com.project.projectmanagment.entities.user.UserEntity;
import com.project.projectmanagment.models.project.*;
import com.project.projectmanagment.models.response.ApiResponse;
import com.project.projectmanagment.repositories.project.*;
import com.project.projectmanagment.repositories.task.TaskRepository;
import com.project.projectmanagment.repositories.user.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository memberRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final EmailService emailService;

    @Transactional
    public ApiResponse createProject(ProjectDTO request, String creatorEmail) {
        if (projectRepository.existsByProjectName(request.getProjectName())) {
            return ApiResponse.conflict("Project name already exists");
        }

        Optional<UserEntity> creatorOpt = userRepository.findByEmail(creatorEmail);
        if (creatorOpt.isEmpty()) {
            return ApiResponse.notFound("Creator user not found");
        }

        UserEntity creator = creatorOpt.get();

        ProjectEntity project = ProjectEntity.builder()
            .projectName(request.getProjectName())
            .projectDescription(request.getProjectDescription())
            .projectStartDate(request.getProjectStartDate())
            .projectStatus(request.getProjectStatus() != null ? request.getProjectStatus() : "ACTIVE")
            .createdBy(creator.getUserId())
            .build();

        project = projectRepository.save(project);

        ProjectMember adminMember = ProjectMember.builder()
            .userId(creator.getUserId())
            .projectId(project.getProjectId())
            .role("ADMIN")
            .status("ACCEPTED")
            .joinedAt(new Date(System.currentTimeMillis()))
            .build();

        memberRepository.save(adminMember);

        log.info("Project '{}' created by {}", project.getProjectName(), creatorEmail);
        return ApiResponse.success("Project created successfully", toDTO(project, creator));
    }

    public ApiResponse getAllProjects() {
        List<ProjectDTO> projects = projectRepository.findAll().stream()
            .map(p -> toDTO(p, null))
            .collect(Collectors.toList());
        return ApiResponse.success(projects);
    }

    public ApiResponse getProjectByName(String projectName) {
        Optional<ProjectEntity> projectOpt = projectRepository.findByProjectName(projectName);
        if (projectOpt.isEmpty()) {
            return ApiResponse.notFound("Project not found");
        }
        return ApiResponse.success(toDTO(projectOpt.get(), null));
    }

    public ApiResponse getProjectById(Long projectId) {
        Optional<ProjectEntity> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isEmpty()) {
            return ApiResponse.notFound("Project not found");
        }
        return ApiResponse.success(toDTO(projectOpt.get(), null));
    }

    public ApiResponse getUserProjects(String email) {
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ApiResponse.notFound("User not found");
        }

        List<ProjectMember> memberships = memberRepository.findByUserId(userOpt.get().getUserId());
        List<Long> projectIds = memberships.stream()
            .filter(m -> "ACCEPTED".equals(m.getStatus()))
            .map(ProjectMember::getProjectId)
            .collect(Collectors.toList());

        List<ProjectDTO> projects = projectRepository.findAllById(projectIds).stream()
            .map(p -> toDTO(p, null))
            .collect(Collectors.toList());

        return ApiResponse.success(projects);
    }

    @Transactional
    public ApiResponse updateProject(String projectName, ProjectDTO request) {
        Optional<ProjectEntity> projectOpt = projectRepository.findByProjectName(projectName);
        if (projectOpt.isEmpty()) {
            return ApiResponse.notFound("Project not found");
        }

        ProjectEntity project = projectOpt.get();

        if (request.getProjectName() != null && !request.getProjectName().isEmpty()) {
            project.setProjectName(request.getProjectName());
        }
        if (request.getProjectDescription() != null) {
            project.setProjectDescription(request.getProjectDescription());
        }
        if (request.getProjectStartDate() != null) {
            project.setProjectStartDate(request.getProjectStartDate());
        }
        if (request.getProjectStatus() != null) {
            project.setProjectStatus(request.getProjectStatus());
            project.setProjectStatusUpdatedDate(new Date(System.currentTimeMillis()));
        }

        projectRepository.save(project);
        log.info("Project '{}' updated", projectName);
        return ApiResponse.success("Project updated successfully", toDTO(project, null));
    }

    @Transactional
    public ApiResponse deleteProject(String projectName) {
        Optional<ProjectEntity> projectOpt = projectRepository.findByProjectName(projectName);
        if (projectOpt.isEmpty()) {
            return ApiResponse.notFound("Project not found");
        }

        ProjectEntity project = projectOpt.get();
        
        taskRepository.deleteByProjectId(project.getProjectId());
        memberRepository.deleteByProjectId(project.getProjectId());
        projectRepository.delete(project);

        log.info("Project '{}' deleted", projectName);
        return ApiResponse.success("Project deleted successfully", null);
    }

    @Transactional
    public ApiResponse inviteMember(InviteRequest request) {
        Optional<UserEntity> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            return ApiResponse.notFound("User not found. They must register first.");
        }

        Optional<ProjectEntity> projectOpt = projectRepository.findByProjectName(request.getProjectName());
        if (projectOpt.isEmpty()) {
            return ApiResponse.notFound("Project not found");
        }

        UserEntity user = userOpt.get();
        ProjectEntity project = projectOpt.get();

        Optional<ProjectMember> existingMember = memberRepository.findByUserIdAndProjectId(
            user.getUserId(), project.getProjectId()
        );
        if (existingMember.isPresent()) {
            return ApiResponse.conflict("User is already a member or has a pending invitation");
        }

        String role = validateRole(request.getRole());

        ProjectMember member = ProjectMember.builder()
            .userId(user.getUserId())
            .projectId(project.getProjectId())
            .role(role)
            .status("PENDING")
            .joinedAt(new Date(System.currentTimeMillis()))
            .build();

        memberRepository.save(member);

        String inviteLink = "/api/project/accept-invite/" + request.getEmail() + "/" + request.getProjectName();
        emailService.sendProjectInvitation(request.getEmail(), request.getProjectName(), 
            request.getInvitedBy(), inviteLink);

        log.info("Invitation sent to {} for project '{}' with role {}", 
            request.getEmail(), request.getProjectName(), role);

        Map<String, Object> response = new HashMap<>();
        response.put("email", request.getEmail());
        response.put("role", role);
        response.put("status", "PENDING");

        return ApiResponse.success("Invitation sent successfully", response);
    }

    @Transactional
    public ApiResponse acceptInvitation(String email, String projectName) {
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ApiResponse.notFound("User not found");
        }

        Optional<ProjectEntity> projectOpt = projectRepository.findByProjectName(projectName);
        if (projectOpt.isEmpty()) {
            return ApiResponse.notFound("Project not found");
        }

        Optional<ProjectMember> memberOpt = memberRepository.findByUserIdAndProjectId(
            userOpt.get().getUserId(), projectOpt.get().getProjectId()
        );
        if (memberOpt.isEmpty()) {
            return ApiResponse.notFound("No invitation found");
        }

        ProjectMember member = memberOpt.get();
        if ("ACCEPTED".equals(member.getStatus())) {
            return ApiResponse.conflict("Invitation already accepted");
        }

        member.setStatus("ACCEPTED");
        member.setJoinedAt(new Date(System.currentTimeMillis()));
        memberRepository.save(member);

        log.info("User {} accepted invitation to project '{}'", email, projectName);
        return ApiResponse.success("Invitation accepted", null);
    }

    public ApiResponse getProjectMembers(String projectName) {
        Optional<ProjectEntity> projectOpt = projectRepository.findByProjectName(projectName);
        if (projectOpt.isEmpty()) {
            return ApiResponse.notFound("Project not found");
        }

        List<ProjectMember> members = memberRepository.findByProjectId(projectOpt.get().getProjectId());
        List<Long> userIds = members.stream().map(ProjectMember::getUserId).collect(Collectors.toList());
        List<UserEntity> users = userRepository.findByUserIdIn(userIds);

        List<ProjectMemberDTO> memberDTOs = members.stream().map(m -> {
            UserEntity user = users.stream()
                .filter(u -> u.getUserId().equals(m.getUserId()))
                .findFirst()
                .orElse(null);

            return ProjectMemberDTO.builder()
                .userId(m.getUserId())
                .email(user != null ? user.getEmail() : null)
                .name(user != null ? user.getName() : null)
                .role(m.getRole())
                .status(m.getStatus())
                .joinedAt(m.getJoinedAt())
                .build();
        }).collect(Collectors.toList());

        return ApiResponse.success(memberDTOs);
    }

    public ApiResponse getMemberRole(String email, String projectName) {
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ApiResponse.notFound("User not found");
        }

        Optional<ProjectEntity> projectOpt = projectRepository.findByProjectName(projectName);
        if (projectOpt.isEmpty()) {
            return ApiResponse.notFound("Project not found");
        }

        Optional<ProjectMember> memberOpt = memberRepository.findByUserIdAndProjectId(
            userOpt.get().getUserId(), projectOpt.get().getProjectId()
        );

        if (memberOpt.isEmpty() || !"ACCEPTED".equals(memberOpt.get().getStatus())) {
            return ApiResponse.notFound("User is not a member of this project");
        }

        Map<String, String> response = new HashMap<>();
        response.put("role", memberOpt.get().getRole());
        response.put("email", email);
        response.put("projectName", projectName);

        return ApiResponse.success(response);
    }

    @Transactional
    public ApiResponse updateMemberRole(String email, String projectName, String newRole) {
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ApiResponse.notFound("User not found");
        }

        Optional<ProjectEntity> projectOpt = projectRepository.findByProjectName(projectName);
        if (projectOpt.isEmpty()) {
            return ApiResponse.notFound("Project not found");
        }

        Optional<ProjectMember> memberOpt = memberRepository.findByUserIdAndProjectId(
            userOpt.get().getUserId(), projectOpt.get().getProjectId()
        );
        if (memberOpt.isEmpty()) {
            return ApiResponse.notFound("User is not a member of this project");
        }

        ProjectMember member = memberOpt.get();
        member.setRole(validateRole(newRole));
        memberRepository.save(member);

        log.info("Role updated for {} in project '{}' to {}", email, projectName, newRole);
        return ApiResponse.success("Role updated successfully", null);
    }

    @Transactional
    public ApiResponse removeMember(String email, String projectName) {
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ApiResponse.notFound("User not found");
        }

        Optional<ProjectEntity> projectOpt = projectRepository.findByProjectName(projectName);
        if (projectOpt.isEmpty()) {
            return ApiResponse.notFound("Project not found");
        }

        Optional<ProjectMember> memberOpt = memberRepository.findByUserIdAndProjectId(
            userOpt.get().getUserId(), projectOpt.get().getProjectId()
        );
        if (memberOpt.isEmpty()) {
            return ApiResponse.notFound("User is not a member of this project");
        }

        memberRepository.delete(memberOpt.get());
        log.info("Member {} removed from project '{}'", email, projectName);
        return ApiResponse.success("Member removed successfully", null);
    }

    private String validateRole(String role) {
        if (role == null || role.isEmpty()) {
            return "MEMBRE";
        }
        String upperRole = role.toUpperCase();
        if (upperRole.equals("ADMIN") || upperRole.equals("ADMINISTRATEUR")) {
            return "ADMIN";
        }
        if (upperRole.equals("MEMBRE") || upperRole.equals("MEMBER")) {
            return "MEMBRE";
        }
        if (upperRole.equals("OBSERVATEUR") || upperRole.equals("OBSERVER")) {
            return "OBSERVATEUR";
        }
        return "MEMBRE";
    }

    private ProjectDTO toDTO(ProjectEntity project, UserEntity creator) {
        return ProjectDTO.builder()
            .projectId(project.getProjectId())
            .projectName(project.getProjectName())
            .projectDescription(project.getProjectDescription())
            .projectStartDate(project.getProjectStartDate())
            .projectStatus(project.getProjectStatus())
            .createdBy(project.getCreatedBy())
            .creatorEmail(creator != null ? creator.getEmail() : null)
            .build();
    }
}
