package com.example.migration.dto;

import com.example.migration.domain.Course;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseResponse {
    private int id;
    private String name;
    private double price;

    public static CourseResponse from(Course course) {
        return new CourseResponse(course.getId(), course.getName(), course.getPrice());
    }
}