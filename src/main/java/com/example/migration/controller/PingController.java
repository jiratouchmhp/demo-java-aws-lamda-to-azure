package com.example.migration.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/ping")
@Tag(name = "Health Check", description = "Health check endpoint")
public class PingController {

    @GetMapping
    @Operation(summary = "Health check", description = "Returns a simple pong response")
    public Map<String, String> ping() {
        return Map.of("pong", "Hello, World!");
    }
}