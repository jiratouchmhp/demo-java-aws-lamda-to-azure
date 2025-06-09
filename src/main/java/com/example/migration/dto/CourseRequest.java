package com.example.migration.dto;

import com.example.migration.domain.Course;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseRequest {
    
    @NotNull(message = "Course ID is required")
    @Min(value = 1, message = "Course ID must be positive")
    private Integer id;
    
    @NotBlank(message = "Course name is required")
    private String name;
    
    @Min(value = 0, message = "Course price must be non-negative")
    private Double price;

    public Course toEntity() {
        return new Course(id, name, price);
    }
}