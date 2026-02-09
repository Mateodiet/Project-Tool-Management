package com.project.projectmanagment.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class HealthControllerTest {

    private final HealthController healthController = new HealthController();

    @Test
    void health_ShouldReturn200() {
        ResponseEntity<Map<String, Object>> result = healthController.health();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("UP", result.getBody().get("status"));
        assertEquals("PMT - Project Management Tool", result.getBody().get("application"));
        assertEquals("1.0.0", result.getBody().get("version"));
    }
}
