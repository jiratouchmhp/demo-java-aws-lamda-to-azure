package com.example.migration.adapter.web.exception;

/**
 * Custom exception for Course not found scenarios
 */
public class CourseNotFoundException extends RuntimeException {
    
    public CourseNotFoundException(Long id) {
        super("Course not found with ID: " + id);
    }
    
    public CourseNotFoundException(String message) {
        super(message);
    }
}