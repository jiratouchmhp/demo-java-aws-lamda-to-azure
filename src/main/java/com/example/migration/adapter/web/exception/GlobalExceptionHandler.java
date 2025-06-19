package com.example.migration.adapter.web.exception;

import com.example.migration.adapter.web.dto.ApiErrorDto;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

/**
 * Global Exception Handler for the application
 * Following Clean Architecture and custom instructions
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    @ExceptionHandler(CourseNotFoundException.class)
    public ResponseEntity<ApiErrorDto> handleCourseNotFound(CourseNotFoundException ex) {
        logger.warn("Course not found: {}", ex.getMessage());
        
        ApiErrorDto error = new ApiErrorDto(
            ex.getMessage(),
            "COURSE_NOT_FOUND",
            HttpStatus.NOT_FOUND.value(),
            LocalDateTime.now().format(TIMESTAMP_FORMATTER)
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDto> handleValidation(MethodArgumentNotValidException ex) {
        logger.warn("Validation error: {}", ex.getMessage());
        
        String message = ex.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", "));
        
        ApiErrorDto error = new ApiErrorDto(
            message,
            "VALIDATION_ERROR",
            HttpStatus.BAD_REQUEST.value(),
            LocalDateTime.now().format(TIMESTAMP_FORMATTER)
        );
        
        return ResponseEntity.badRequest().body(error);
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorDto> handleConstraintViolation(ConstraintViolationException ex) {
        logger.warn("Constraint violation: {}", ex.getMessage());
        
        String message = ex.getConstraintViolations().stream()
            .map(violation -> violation.getMessage())
            .collect(Collectors.joining(", "));
        
        ApiErrorDto error = new ApiErrorDto(
            message,
            "CONSTRAINT_VIOLATION",
            HttpStatus.BAD_REQUEST.value(),
            LocalDateTime.now().format(TIMESTAMP_FORMATTER)
        );
        
        return ResponseEntity.badRequest().body(error);
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorDto> handleIllegalArgument(IllegalArgumentException ex) {
        logger.warn("Illegal argument: {}", ex.getMessage());
        
        ApiErrorDto error = new ApiErrorDto(
            ex.getMessage(),
            "ILLEGAL_ARGUMENT",
            HttpStatus.BAD_REQUEST.value(),
            LocalDateTime.now().format(TIMESTAMP_FORMATTER)
        );
        
        return ResponseEntity.badRequest().body(error);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDto> handleGeneric(Exception ex) {
        logger.error("Unexpected error occurred", ex);
        
        ApiErrorDto error = new ApiErrorDto(
            "An unexpected error occurred",
            "INTERNAL_ERROR",
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            LocalDateTime.now().format(TIMESTAMP_FORMATTER)
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}