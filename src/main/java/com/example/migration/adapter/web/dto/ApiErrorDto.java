package com.example.migration.adapter.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Standard API Error Response DTO
 */
@Schema(description = "API Error Response")
public record ApiErrorDto(
    
    @Schema(description = "Error message", example = "Course not found")
    String message,
    
    @Schema(description = "Error code", example = "COURSE_NOT_FOUND")
    String code,
    
    @Schema(description = "HTTP status code", example = "404")
    int status,
    
    @Schema(description = "Timestamp of the error")
    String timestamp
) {
}