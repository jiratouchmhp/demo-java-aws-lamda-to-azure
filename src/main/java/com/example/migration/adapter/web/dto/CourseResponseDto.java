package com.example.migration.adapter.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Course Response DTO using Java record
 * Following Clean Architecture - adapter layer DTO
 */
@Schema(description = "Course response")
public record CourseResponseDto(
    
    @Schema(description = "Course ID", example = "1")
    Long id,
    
    @Schema(description = "Course name", example = "Spring Boot Fundamentals")
    String name,
    
    @Schema(description = "Course price", example = "299.99")
    BigDecimal price,
    
    @Schema(description = "Course description", example = "Learn Spring Boot from scratch")
    String description,
    
    @Schema(description = "Created timestamp")
    LocalDateTime createdAt,
    
    @Schema(description = "Last updated timestamp")
    LocalDateTime updatedAt,
    
    @Schema(description = "Created by user")
    String createdBy,
    
    @Schema(description = "Updated by user")
    String updatedBy,
    
    @Schema(description = "Whether the course is expensive (>$1000)")
    boolean expensive
) {
}