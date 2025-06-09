package com.example.migration.domain;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Course domain objects.
 * This is a port in clean architecture - defines the contract
 * but implementation will be in the adapter layer.
 */
public interface CourseRepository {
    void save(Course course);
    List<Course> findAll();
    Optional<Course> findById(int id);
    boolean update(int id, Course course);
    boolean deleteById(int id);
}