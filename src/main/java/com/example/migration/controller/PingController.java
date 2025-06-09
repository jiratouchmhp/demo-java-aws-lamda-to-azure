package com.example.migration.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/ping")
@Tag(name = "Health Check", description = "Health check endpoints")
@Slf4j
public class PingController {

    @GetMapping
    @Operation(summary = "Health check endpoint", description = "Returns a simple ping response")
    public ResponseEntity<Map<String, String>> ping() {
        log.info("Ping endpoint called");
        return ResponseEntity.ok(Map.of("pong", "Hello, World!"));
    }
}