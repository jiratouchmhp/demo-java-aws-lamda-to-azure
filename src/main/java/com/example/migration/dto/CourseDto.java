package com.example.migration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Course API operations
 * This isolates the API contract from the domain model
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Course information")
public class CourseDto {
    
    @Schema(description = "Course ID", example = "1")
    private int id;
    
    @NotBlank(message = "Course name is required")
    @Schema(description = "Course name", example = "Spring Boot Fundamentals")
    private String name;
    
    @Positive(message = "Course price must be positive")
    @Schema(description = "Course price", example = "99.99")
    private double price;
}