package com.example.migration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Course request/response DTO")
public class CourseDto {
    
    @Schema(description = "Course ID", example = "1")
    @NotNull(message = "Course ID is required")
    @Positive(message = "Course ID must be positive")
    private int id;
    
    @Schema(description = "Course name", example = "Spring Boot Fundamentals")
    @NotBlank(message = "Course name is required")
    private String name;
    
    @Schema(description = "Course price", example = "99.99")
    @NotNull(message = "Course price is required")
    @Positive(message = "Course price must be positive")
    private double price;
}