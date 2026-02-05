package com.project.projectmanagment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.projectmanagment.dto.ProjectDTO;
import com.project.projectmanagment.services.ProjectService;
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

@WebMvcTest(ProjectController.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProjectDTO testProject;

    @BeforeEach
    void setUp() {
        testProject = ProjectDTO.builder()
                .projectId(1L)
                .projectName("Test Project")
                .projectDescription("Test Description")
                .projectStatus("ACTIVE")
                .createdBy(1L)
                .build();
    }

    @Test
    void getAllProjects_ShouldReturnProjects() throws Exception {
        when(projectService.getAllProjects())
                .thenReturn(ApiResponse.success(Arrays.asList(testProject)));

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"));
    }

    @Test
    void getProjectByName_ShouldReturnProject() throws Exception {
        when(projectService.getProjectByName("Test Project"))
                .thenReturn(ApiResponse.success(testProject));

        mockMvc.perform(get("/api/projects/Test Project"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.projectName").value("Test Project"));
    }

    @Test
    void getProjectByName_NotFound_ShouldReturn404() throws Exception {
        when(projectService.getProjectByName("Unknown"))
                .thenReturn(ApiResponse.notFound("Project not found"));

        mockMvc.perform(get("/api/projects/Unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createProject_ShouldReturnCreated() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("projectName", "New Project");
        request.put("projectDescription", "Description");
        request.put("email", "test@test.com");

        when(projectService.createProject(any(), anyString()))
                .thenReturn(ApiResponse.created("Project created", testProject));

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void deleteProject_ShouldReturnOk() throws Exception {
        when(projectService.deleteProject("Test Project"))
                .thenReturn(ApiResponse.success("Deleted", null));

        mockMvc.perform(delete("/api/projects/Test Project"))
                .andExpect(status().isOk());
    }

    @Test
    void inviteMember_ShouldReturnOk() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("email", "member@test.com");
        request.put("role", "MEMBRE");

        when(projectService.inviteMember(anyString(), anyString(), anyString()))
                .thenReturn(ApiResponse.success("Invited", null));

        mockMvc.perform(post("/api/projects/Test Project/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void getProjectMembers_ShouldReturnMembers() throws Exception {
        when(projectService.getProjectMembers("Test Project"))
                .thenReturn(ApiResponse.success(Arrays.asList()));

        mockMvc.perform(get("/api/projects/Test Project/members"))
                .andExpect(status().isOk());
    }

    @Test
    void getMemberRole_ShouldReturnRole() throws Exception {
        Map<String, String> roleData = new HashMap<>();
        roleData.put("role", "ADMIN");

        when(projectService.getMemberRole(anyString(), anyString()))
                .thenReturn(ApiResponse.success(roleData));

        mockMvc.perform(get("/api/projects/Test Project/members/test@test.com/role"))
                .andExpect(status().isOk());
    }
}