package com.example.migration.adapter.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Health check controller
 * Maintains compatibility with existing ping endpoint
 */
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Health Check", description = "Application health check endpoints")
public class PingController {
    
    @Operation(summary = "Health check ping", description = "Simple health check endpoint")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Application is healthy")
    })
    @GetMapping("/ping")
    public ResponseEntity<Map<String, Object>> ping() {
        return ResponseEntity.ok(Map.of(
            "status", "healthy",
            "message", "Hello, World!",
            "timestamp", LocalDateTime.now(),
            "service", "Course Management API"
        ));
    }
}