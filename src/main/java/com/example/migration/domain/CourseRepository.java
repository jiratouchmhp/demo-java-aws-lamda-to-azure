package com.example.migration.domain;

import java.util.List;
import java.util.Optional;

public interface CourseRepository {
    Course save(Course course);
    Optional<Course> findById(int id);
    List<Course> findAll();
    boolean deleteById(int id);
    boolean updateCourse(int id, Course course);
}