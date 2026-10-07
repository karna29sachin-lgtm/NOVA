package com.example.controlsystem.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/test")
@Tag(name = "Test API", description = "Endpoints for testing frontend-backend connection")
public class TestController {

    @GetMapping("/ping")
    @Operation(summary = "Ping the backend", description = "Returns a simple greeting message")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("Backend is connected successfully!");
    }
}
