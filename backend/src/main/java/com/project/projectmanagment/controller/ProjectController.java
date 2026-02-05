package com.project.projectmanagment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.projectmanagment.models.project.*;
import com.project.projectmanagment.models.response.ApiResponse;
import com.project.projectmanagment.services.ProjectService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/project")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createProject(@RequestBody ProjectDTO request, 
            @RequestParam String creatorEmail) {
        ApiResponse response = projectService.createProject(request, creatorEmail);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllProjects() {
        ApiResponse response = projectService.getAllProjects();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/name/{projectName}")
    public ResponseEntity<ApiResponse> getProjectByName(@PathVariable String projectName) {
        ApiResponse response = projectService.getProjectByName(projectName);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ApiResponse> getProjectById(@PathVariable Long projectId) {
        ApiResponse response = projectService.getProjectById(projectId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/user/{email}")
    public ResponseEntity<ApiResponse> getUserProjects(@PathVariable String email) {
        ApiResponse response = projectService.getUserProjects(email);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/{projectName}")
    public ResponseEntity<ApiResponse> updateProject(@PathVariable String projectName, 
            @RequestBody ProjectDTO request) {
        ApiResponse response = projectService.updateProject(projectName, request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/{projectName}")
    public ResponseEntity<ApiResponse> deleteProject(@PathVariable String projectName) {
        ApiResponse response = projectService.deleteProject(projectName);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/invite")
    public ResponseEntity<ApiResponse> inviteMember(@RequestBody InviteRequest request) {
        ApiResponse response = projectService.inviteMember(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/accept-invite/{email}/{projectName}")
    public ResponseEntity<ApiResponse> acceptInvitation(@PathVariable String email, 
            @PathVariable String projectName) {
        ApiResponse response = projectService.acceptInvitation(email, projectName);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/{projectName}/members")
    public ResponseEntity<ApiResponse> getProjectMembers(@PathVariable String projectName) {
        ApiResponse response = projectService.getProjectMembers(projectName);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/{projectName}/member-role/{email}")
    public ResponseEntity<ApiResponse> getMemberRole(@PathVariable String projectName, 
            @PathVariable String email) {
        ApiResponse response = projectService.getMemberRole(email, projectName);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/{projectName}/member-role/{email}")
    public ResponseEntity<ApiResponse> updateMemberRole(@PathVariable String projectName, 
            @PathVariable String email, @RequestParam String role) {
        ApiResponse response = projectService.updateMemberRole(email, projectName, role);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/{projectName}/member/{email}")
    public ResponseEntity<ApiResponse> removeMember(@PathVariable String projectName, 
            @PathVariable String email) {
        ApiResponse response = projectService.removeMember(email, projectName);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
