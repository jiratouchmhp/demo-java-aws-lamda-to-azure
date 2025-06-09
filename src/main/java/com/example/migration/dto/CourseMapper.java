package com.example.migration.dto;

import com.example.migration.domain.Course;

/**
 * Mapper utility to convert between Domain objects and DTOs
 */
public class CourseMapper {

    public static Course toDomain(CourseDto dto) {
        return new Course(dto.getId(), dto.getName(), dto.getPrice());
    }

    public static CourseDto toDto(Course domain) {
        return new CourseDto(domain.getId(), domain.getName(), domain.getPrice());
    }
}