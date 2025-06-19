package com.example.migration.domain.port;

import com.example.migration.domain.model.Course;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface (Port) for Course domain
 * Following Clean Architecture - this is the contract defined in domain layer
 */
public interface CourseRepository {
    
    /**
     * Save a course
     */
    Course save(Course course);
    
    /**
     * Find all courses
     */
    List<Course> findAll();
    
    /**
     * Find course by ID
     */
    Optional<Course> findById(Long id);
    
    /**
     * Delete course by ID
     */
    boolean deleteById(Long id);
    
    /**
     * Check if course exists by ID
     */
    boolean existsById(Long id);
    
    /**
     * Find courses by name containing (case-insensitive)
     */
    List<Course> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find courses by price range
     */
    List<Course> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    /**
     * Count total courses
     */
    long count();
}