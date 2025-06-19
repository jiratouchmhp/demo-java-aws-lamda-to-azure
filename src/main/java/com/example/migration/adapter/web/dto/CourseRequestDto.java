package com.example.migration.adapter.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * Course Request DTO using Java record
 * Following Clean Architecture - adapter layer DTO
 */
@Schema(description = "Course creation/update request")
public record CourseRequestDto(
    
    @Schema(description = "Course name", example = "Spring Boot Fundamentals")
    @NotBlank(message = "Course name is required")
    @Size(min = 2, max = 100, message = "Course name must be between 2 and 100 characters")
    String name,
    
    @Schema(description = "Course price", example = "299.99")
    @NotNull(message = "Course price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Course price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Course price must have at most 2 decimal places")
    BigDecimal price,
    
    @Schema(description = "Course description", example = "Learn Spring Boot from scratch")
    @Size(max = 500, message = "Course description must not exceed 500 characters")
    String description
) {
}