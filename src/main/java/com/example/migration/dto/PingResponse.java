package com.example.migration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for ping endpoint
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Ping response")
public class PingResponse {
    
    @Schema(description = "Pong message", example = "Hello, World!")
    private String pong;
}