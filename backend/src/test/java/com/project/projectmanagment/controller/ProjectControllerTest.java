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

import com.project.projectmanagment.models.project.*;
import com.project.projectmanagment.models.response.ApiResponse;
import com.project.projectmanagment.services.ProjectService;

@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProjectController projectController;

    private ApiResponse okResponse;
    private ApiResponse notFoundResponse;

    @BeforeEach
    void setUp() {
        okResponse = ApiResponse.builder().status(HttpStatus.OK).message("Success").build();
        notFoundResponse = ApiResponse.builder().status(HttpStatus.NOT_FOUND).message("Not found").build();
    }

    @Test
    void createProject_ShouldReturn200() {
        when(projectService.createProject(any(), anyString())).thenReturn(okResponse);

        ProjectDTO dto = ProjectDTO.builder().projectName("Test").build();
        ResponseEntity<ApiResponse> result = projectController.createProject(dto, "test@test.com");

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void getAllProjects_ShouldReturn200() {
        when(projectService.getAllProjects()).thenReturn(okResponse);

        ResponseEntity<ApiResponse> result = projectController.getAllProjects();

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void getProjectByName_ShouldReturn200() {
        when(projectService.getProjectByName(anyString())).thenReturn(okResponse);

        ResponseEntity<ApiResponse> result = projectController.getProjectByName("Test");

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void getProjectById_ShouldReturn200() {
        when(projectService.getProjectById(1L)).thenReturn(okResponse);

        ResponseEntity<ApiResponse> result = projectController.getProjectById(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void getUserProjects_ShouldReturn200() {
        when(projectService.getUserProjects(anyString())).thenReturn(okResponse);

        ResponseEntity<ApiResponse> result = projectController.getUserProjects("test@test.com");

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void updateProject_ShouldReturn200() {
        when(projectService.updateProject(anyString(), any())).thenReturn(okResponse);

        ProjectDTO dto = ProjectDTO.builder().projectName("Updated").build();
        ResponseEntity<ApiResponse> result = projectController.updateProject("Test", dto);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void deleteProject_ShouldReturn200() {
        when(projectService.deleteProject(anyString())).thenReturn(okResponse);

        ResponseEntity<ApiResponse> result = projectController.deleteProject("Test");

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void inviteMember_ShouldReturn200() {
        when(projectService.inviteMember(any())).thenReturn(okResponse);

        InviteRequest request = InviteRequest.builder()
            .email("user@test.com").projectName("Test").role("MEMBRE").invitedBy("admin@test.com").build();
        ResponseEntity<ApiResponse> result = projectController.inviteMember(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void acceptInvitation_ShouldReturn200() {
        when(projectService.acceptInvitation(anyString(), anyString())).thenReturn(okResponse);

        ResponseEntity<ApiResponse> result = projectController.acceptInvitation("test@test.com", "Test");

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void getProjectMembers_ShouldReturn200() {
        when(projectService.getProjectMembers(anyString())).thenReturn(okResponse);

        ResponseEntity<ApiResponse> result = projectController.getProjectMembers("Test");

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void getMemberRole_ShouldReturn200() {
        when(projectService.getMemberRole(anyString(), anyString())).thenReturn(okResponse);

        ResponseEntity<ApiResponse> result = projectController.getMemberRole("Test", "test@test.com");

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void updateMemberRole_ShouldReturn200() {
        when(projectService.updateMemberRole(anyString(), anyString(), anyString())).thenReturn(okResponse);

        ResponseEntity<ApiResponse> result = projectController.updateMemberRole("Test", "test@test.com", "ADMIN");

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void removeMember_ShouldReturn200() {
        when(projectService.removeMember(anyString(), anyString())).thenReturn(okResponse);

        ResponseEntity<ApiResponse> result = projectController.removeMember("Test", "test@test.com");

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }
}
