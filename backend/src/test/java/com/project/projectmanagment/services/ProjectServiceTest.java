package com.project.projectmanagment.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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

import com.project.projectmanagment.entities.bridges.ProjectMember;
import com.project.projectmanagment.entities.project.ProjectEntity;
import com.project.projectmanagment.entities.user.UserEntity;
import com.project.projectmanagment.models.project.*;
import com.project.projectmanagment.models.response.ApiResponse;
import com.project.projectmanagment.repositories.project.*;
import com.project.projectmanagment.repositories.task.TaskRepository;
import com.project.projectmanagment.repositories.user.UserRepository;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ProjectMemberRepository memberRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private ProjectService projectService;

    private ProjectEntity testProject;
    private UserEntity testUser;
    private ProjectDTO projectDTO;

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
            .projectDescription("Description")
            .projectStatus("ACTIVE")
            .createdBy(1L)
            .build();

        projectDTO = ProjectDTO.builder()
            .projectName("Test Project")
            .projectDescription("Description")
            .projectStatus("ACTIVE")
            .build();
    }

    @Test
    void createProject_Success() {
        when(projectRepository.existsByProjectName(anyString())).thenReturn(false);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(projectRepository.save(any(ProjectEntity.class))).thenReturn(testProject);
        when(memberRepository.save(any(ProjectMember.class))).thenReturn(new ProjectMember());

        ApiResponse response = projectService.createProject(projectDTO, "test@example.com");

        assertEquals(HttpStatus.OK, response.getStatus());
        verify(memberRepository).save(any(ProjectMember.class));
    }

    @Test
    void createProject_NameExists() {
        when(projectRepository.existsByProjectName(anyString())).thenReturn(true);

        ApiResponse response = projectService.createProject(projectDTO, "test@example.com");

        assertEquals(HttpStatus.CONFLICT, response.getStatus());
    }

    @Test
    void getAllProjects_Success() {
        when(projectRepository.findAll()).thenReturn(Arrays.asList(testProject));

        ApiResponse response = projectService.getAllProjects();

        assertEquals(HttpStatus.OK, response.getStatus());
        List<?> projects = (List<?>) response.getData();
        assertEquals(1, projects.size());
    }

    @Test
    void getProjectByName_Success() {
        when(projectRepository.findByProjectName(anyString())).thenReturn(Optional.of(testProject));

        ApiResponse response = projectService.getProjectByName("Test Project");

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void getProjectByName_NotFound() {
        when(projectRepository.findByProjectName(anyString())).thenReturn(Optional.empty());

        ApiResponse response = projectService.getProjectByName("Unknown");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
    }

    @Test
    void updateProject_Success() {
        when(projectRepository.findByProjectName(anyString())).thenReturn(Optional.of(testProject));
        when(projectRepository.save(any(ProjectEntity.class))).thenReturn(testProject);

        ApiResponse response = projectService.updateProject("Test Project", projectDTO);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void deleteProject_Success() {
        when(projectRepository.findByProjectName(anyString())).thenReturn(Optional.of(testProject));
        doNothing().when(taskRepository).deleteByProjectId(anyLong());
        doNothing().when(memberRepository).deleteByProjectId(anyLong());
        doNothing().when(projectRepository).delete(any(ProjectEntity.class));

        ApiResponse response = projectService.deleteProject("Test Project");

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void inviteMember_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(projectRepository.findByProjectName(anyString())).thenReturn(Optional.of(testProject));
        when(memberRepository.findByUserIdAndProjectId(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(memberRepository.save(any(ProjectMember.class))).thenReturn(new ProjectMember());
        doNothing().when(emailService).sendProjectInvitation(anyString(), anyString(), anyString(), anyString());

        InviteRequest request = InviteRequest.builder()
            .email("invitee@example.com")
            .projectName("Test Project")
            .role("MEMBER")
            .invitedBy("test@example.com")
            .build();

        ApiResponse response = projectService.inviteMember(request);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void acceptInvitation_Success() {
        ProjectMember member = ProjectMember.builder()
            .userId(1L)
            .projectId(1L)
            .status("PENDING")
            .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(projectRepository.findByProjectName(anyString())).thenReturn(Optional.of(testProject));
        when(memberRepository.findByUserIdAndProjectId(anyLong(), anyLong())).thenReturn(Optional.of(member));
        when(memberRepository.save(any(ProjectMember.class))).thenReturn(member);

        ApiResponse response = projectService.acceptInvitation("test@example.com", "Test Project");

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void getProjectMembers_Success() {
        ProjectMember member = ProjectMember.builder()
            .userId(1L)
            .projectId(1L)
            .role("ADMIN")
            .status("ACCEPTED")
            .build();

        when(projectRepository.findByProjectName(anyString())).thenReturn(Optional.of(testProject));
        when(memberRepository.findByProjectId(anyLong())).thenReturn(Arrays.asList(member));
        when(userRepository.findByUserIdIn(anyList())).thenReturn(Arrays.asList(testUser));

        ApiResponse response = projectService.getProjectMembers("Test Project");

        assertEquals(HttpStatus.OK, response.getStatus());
    }
}
