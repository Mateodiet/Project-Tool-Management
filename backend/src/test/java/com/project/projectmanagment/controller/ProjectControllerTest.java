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
import com.project.projectmanagment.services.ProjectService;

@WebMvcTest(ProjectController.class)
@Import(SecurityConfig.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @Test
    void createProject_ShouldReturn200() throws Exception {
        ApiResponse response = ApiResponse.builder()
            .status(HttpStatus.OK)
            .message("Project created")
            .build();

        when(projectService.createProject(any(), anyString())).thenReturn(response);

        mockMvc.perform(post("/api/project/create")
                .param("creatorEmail", "test@test.com")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"projectName\":\"Test\",\"projectDescription\":\"Desc\",\"projectStatus\":\"ACTIVE\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Project created"));
    }

    @Test
    void getAllProjects_ShouldReturn200() throws Exception {
        ApiResponse response = ApiResponse.builder()
            .status(HttpStatus.OK)
            .message("Projects retrieved")
            .build();

        when(projectService.getAllProjects()).thenReturn(response);

        mockMvc.perform(get("/api/project/all"))
            .andExpect(status().isOk());
    }

    @Test
    void getProjectByName_ShouldReturn200() throws Exception {
        ApiResponse response = ApiResponse.builder()
            .status(HttpStatus.OK)
            .message("Project found")
            .build();

        when(projectService.getProjectByName(anyString())).thenReturn(response);

        mockMvc.perform(get("/api/project/name/TestProject"))
            .andExpect(status().isOk());
    }

    @Test
    void getProjectByName_NotFound() throws Exception {
        ApiResponse response = ApiResponse.builder()
            .status(HttpStatus.NOT_FOUND)
            .message("Project not found")
            .build();

        when(projectService.getProjectByName(anyString())).thenReturn(response);

        mockMvc.perform(get("/api/project/name/Unknown"))
            .andExpect(status().isNotFound());
    }

    @Test
    void updateProject_ShouldReturn200() throws Exception {
        ApiResponse response = ApiResponse.builder()
            .status(HttpStatus.OK)
            .message("Project updated")
            .build();

        when(projectService.updateProject(anyString(), any())).thenReturn(response);

        mockMvc.perform(put("/api/project/TestProject")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"projectName\":\"Updated\",\"projectDescription\":\"Desc\",\"projectStatus\":\"ACTIVE\"}"))
            .andExpect(status().isOk());
    }

    @Test
    void deleteProject_ShouldReturn200() throws Exception {
        ApiResponse response = ApiResponse.builder()
            .status(HttpStatus.OK)
            .message("Project deleted")
            .build();

        when(projectService.deleteProject(anyString())).thenReturn(response);

        mockMvc.perform(delete("/api/project/TestProject"))
            .andExpect(status().isOk());
    }

    @Test
    void inviteMember_ShouldReturn200() throws Exception {
        ApiResponse response = ApiResponse.builder()
            .status(HttpStatus.OK)
            .message("Invitation sent")
            .build();

        when(projectService.inviteMember(any())).thenReturn(response);

        mockMvc.perform(post("/api/project/invite")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"user@test.com\",\"projectName\":\"Test\",\"role\":\"MEMBRE\",\"invitedBy\":\"admin@test.com\"}"))
            .andExpect(status().isOk());
    }

    @Test
    void acceptInvitation_ShouldReturn200() throws Exception {
        ApiResponse response = ApiResponse.builder()
            .status(HttpStatus.OK)
            .message("Invitation accepted")
            .build();

        when(projectService.acceptInvitation(anyString(), anyString())).thenReturn(response);

        mockMvc.perform(get("/api/project/accept-invite/test@test.com/TestProject"))
            .andExpect(status().isOk());
    }

    @Test
    void getProjectMembers_ShouldReturn200() throws Exception {
        ApiResponse response = ApiResponse.builder()
            .status(HttpStatus.OK)
            .message("Members retrieved")
            .build();

        when(projectService.getProjectMembers(anyString())).thenReturn(response);

        mockMvc.perform(get("/api/project/TestProject/members"))
            .andExpect(status().isOk());
    }

    @Test
    void getMemberRole_ShouldReturn200() throws Exception {
        ApiResponse response = ApiResponse.builder()
            .status(HttpStatus.OK)
            .message("Role retrieved")
            .build();

        when(projectService.getMemberRole(anyString(), anyString())).thenReturn(response);

        mockMvc.perform(get("/api/project/TestProject/member-role/test@test.com"))
            .andExpect(status().isOk());
    }
}
