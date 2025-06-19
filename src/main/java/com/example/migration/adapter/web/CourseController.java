package com.example.migration.adapter.web;

import com.example.migration.adapter.web.dto.CourseRequestDto;
import com.example.migration.adapter.web.dto.CourseResponseDto;
import com.example.migration.adapter.web.exception.CourseNotFoundException;
import com.example.migration.adapter.web.mapper.CourseMapper;
import com.example.migration.application.service.CourseService;
import com.example.migration.domain.model.Course;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Course REST Controller
 * Following Clean Architecture - adapter layer
 */
@RestController
@RequestMapping("/api/v1/courses")
@Tag(name = "Course Management", description = "APIs for managing courses")
public class CourseController {
    
    private static final Logger logger = LoggerFactory.getLogger(CourseController.class);
    
    private final CourseService courseService;
    private final CourseMapper courseMapper;
    private final Tracer tracer;
    
    @Autowired
    public CourseController(CourseService courseService, CourseMapper courseMapper, Tracer tracer) {
        this.courseService = courseService;
        this.courseMapper = courseMapper;
        this.tracer = tracer;
    }
    
    @Operation(summary = "Create a new course", description = "Creates a new course in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Course created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<CourseResponseDto> createCourse(
            @Parameter(description = "Course data", required = true)
            @Valid @RequestBody CourseRequestDto courseRequest) {
        
        Span span = tracer.spanBuilder("CourseController.createCourse").startSpan();
        try (var scope = span.makeCurrent()) {
            logger.info("Creating new course: {}", courseRequest.name());
            
            Course course = courseMapper.toEntity(courseRequest);
            Course savedCourse = courseService.createCourse(course);
            CourseResponseDto response = courseMapper.toResponseDto(savedCourse);
            
            logger.info("Successfully created course with ID: {}", savedCourse.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception ex) {
            span.setStatus(StatusCode.ERROR);
            span.recordException(ex);
            logger.error("Error creating course", ex);
            throw ex;
        } finally {
            span.end();
        }
    }
    
    @Operation(summary = "Get all courses", description = "Retrieves all courses from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Courses retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<CourseResponseDto>> getAllCourses() {
        Span span = tracer.spanBuilder("CourseController.getAllCourses").startSpan();
        try (var scope = span.makeCurrent()) {
            logger.debug("Retrieving all courses");
            
            List<Course> courses = courseService.getAllCourses();
            List<CourseResponseDto> response = courseMapper.toResponseDtos(courses);
            
            logger.info("Retrieved {} courses", courses.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            span.setStatus(StatusCode.ERROR);
            span.recordException(ex);
            logger.error("Error retrieving all courses", ex);
            throw ex;
        } finally {
            span.end();
        }
    }
    
    @Operation(summary = "Get course by ID", description = "Retrieves a specific course by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Course found and retrieved"),
        @ApiResponse(responseCode = "404", description = "Course not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponseDto> getCourseById(
            @Parameter(description = "Course ID", required = true, example = "1")
            @PathVariable Long id) {
        
        Span span = tracer.spanBuilder("CourseController.getCourseById").startSpan();
        try (var scope = span.makeCurrent()) {
            logger.debug("Retrieving course by ID: {}", id);
            
            Optional<Course> course = courseService.getCourseById(id);
            if (course.isEmpty()) {
                throw new CourseNotFoundException(id);
            }
            
            CourseResponseDto response = courseMapper.toResponseDto(course.get());
            
            logger.info("Found course with ID: {}", id);
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            span.setStatus(StatusCode.ERROR);
            span.recordException(ex);
            logger.error("Error retrieving course by ID: {}", id, ex);
            throw ex;
        } finally {
            span.end();
        }
    }
    
    @Operation(summary = "Update course", description = "Updates an existing course")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Course updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Course not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CourseResponseDto> updateCourse(
            @Parameter(description = "Course ID", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Updated course data", required = true)
            @Valid @RequestBody CourseRequestDto courseRequest) {
        
        Span span = tracer.spanBuilder("CourseController.updateCourse").startSpan();
        try (var scope = span.makeCurrent()) {
            logger.info("Updating course with ID: {}", id);
            
            Course course = courseMapper.toEntity(courseRequest);
            Course updatedCourse = courseService.updateCourse(id, course);
            CourseResponseDto response = courseMapper.toResponseDto(updatedCourse);
            
            logger.info("Successfully updated course with ID: {}", id);
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException ex) {
            if (ex.getMessage().contains("not found")) {
                throw new CourseNotFoundException(id);
            }
            throw ex;
        } catch (Exception ex) {
            span.setStatus(StatusCode.ERROR);
            span.recordException(ex);
            logger.error("Error updating course with ID: {}", id, ex);
            throw ex;
        } finally {
            span.end();
        }
    }
    
    @Operation(summary = "Delete course", description = "Deletes a course by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Course deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Course not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(
            @Parameter(description = "Course ID", required = true, example = "1")
            @PathVariable Long id) {
        
        Span span = tracer.spanBuilder("CourseController.deleteCourse").startSpan();
        try (var scope = span.makeCurrent()) {
            logger.info("Deleting course with ID: {}", id);
            
            boolean deleted = courseService.deleteCourse(id);
            if (!deleted) {
                throw new CourseNotFoundException(id);
            }
            
            logger.info("Successfully deleted course with ID: {}", id);
            return ResponseEntity.noContent().build();
            
        } catch (Exception ex) {
            span.setStatus(StatusCode.ERROR);
            span.recordException(ex);
            logger.error("Error deleting course with ID: {}", id, ex);
            throw ex;
        } finally {
            span.end();
        }
    }
    
    @Operation(summary = "Apply discount to course", description = "Applies a percentage discount to a course")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Discount applied successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid discount percentage"),
        @ApiResponse(responseCode = "404", description = "Course not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PatchMapping("/{id}/discount")
    public ResponseEntity<CourseResponseDto> applyCourseDiscount(
            @Parameter(description = "Course ID", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Discount percentage", required = true, example = "10.0")
            @RequestParam BigDecimal discountPercentage) {
        
        Span span = tracer.spanBuilder("CourseController.applyCourseDiscount").startSpan();
        try (var scope = span.makeCurrent()) {
            logger.info("Applying {}% discount to course with ID: {}", discountPercentage, id);
            
            if (discountPercentage.compareTo(BigDecimal.ZERO) <= 0 || 
                discountPercentage.compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new IllegalArgumentException("Discount percentage must be between 0 and 100");
            }
            
            Course updatedCourse = courseService.applyCourseDiscount(id, discountPercentage);
            CourseResponseDto response = courseMapper.toResponseDto(updatedCourse);
            
            logger.info("Successfully applied discount to course with ID: {}", id);
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException ex) {
            if (ex.getMessage().contains("not found")) {
                throw new CourseNotFoundException(id);
            }
            throw ex;
        } catch (Exception ex) {
            span.setStatus(StatusCode.ERROR);
            span.recordException(ex);
            logger.error("Error applying discount to course with ID: {}", id, ex);
            throw ex;
        } finally {
            span.end();
        }
    }
}