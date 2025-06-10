package com.example.migration.domain;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface (port) for Course domain entity
 * This defines the contract for persistence operations
 */
public interface CourseRepository {
    void save(Course course);
    List<Course> findAll();
    Optional<Course> findById(int id);
    boolean updateById(int id, Course course);
    boolean deleteById(int id);
}