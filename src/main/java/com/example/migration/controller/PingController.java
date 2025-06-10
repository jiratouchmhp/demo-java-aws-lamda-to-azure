package com.example.migration.controller;

import com.example.migration.dto.PingResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Ping controller for health checks
 */
@RestController
@RequestMapping("/ping")
@Tag(name = "Health Check", description = "Health check endpoints")
public class PingController {
    
    @GetMapping
    @Operation(
        summary = "Health check endpoint",
        description = "Returns a simple pong response to verify the service is running"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Service is healthy",
        content = @Content(schema = @Schema(implementation = PingResponse.class))
    )
    public ResponseEntity<PingResponse> ping() {
        PingResponse response = new PingResponse("Hello, World!");
        return ResponseEntity.ok(response);
    }
}