package com.example.migration.application.service;

import com.example.migration.domain.model.Course;
import com.example.migration.domain.port.CourseRepository;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Course Application Service
 * Contains business logic following Clean Architecture principles
 */
@Service
@Transactional
public class CourseService {
    
    private static final Logger logger = LoggerFactory.getLogger(CourseService.class);
    
    private final CourseRepository courseRepository;
    private final Tracer tracer;
    
    @Autowired
    public CourseService(CourseRepository courseRepository, Tracer tracer) {
        this.courseRepository = courseRepository;
        this.tracer = tracer;
    }
    
    /**
     * Create a new course
     */
    public Course createCourse(Course course) {
        Span span = tracer.spanBuilder("CourseService.createCourse").startSpan();
        try (var scope = span.makeCurrent()) {
            logger.info("Creating new course: {}", course.getName());
            
            Course savedCourse = courseRepository.save(course);
            
            logger.info("Successfully created course with ID: {}", savedCourse.getId());
            return savedCourse;
            
        } catch (Exception ex) {
            span.setStatus(StatusCode.ERROR);
            span.recordException(ex);
            logger.error("Error creating course: {}", course.getName(), ex);
            throw ex;
        } finally {
            span.end();
        }
    }
    
    /**
     * Get all courses
     */
    @Transactional(readOnly = true)
    public List<Course> getAllCourses() {
        Span span = tracer.spanBuilder("CourseService.getAllCourses").startSpan();
        try (var scope = span.makeCurrent()) {
            logger.debug("Retrieving all courses");
            
            List<Course> courses = courseRepository.findAll();
            
            logger.info("Retrieved {} courses", courses.size());
            return courses;
            
        } catch (Exception ex) {
            span.setStatus(StatusCode.ERROR);
            span.recordException(ex);
            logger.error("Error retrieving all courses", ex);
            throw ex;
        } finally {
            span.end();
        }
    }
    
    /**
     * Get course by ID
     */
    @Transactional(readOnly = true)
    public Optional<Course> getCourseById(Long id) {
        Span span = tracer.spanBuilder("CourseService.getCourseById").startSpan();
        try (var scope = span.makeCurrent()) {
            logger.debug("Retrieving course by ID: {}", id);
            
            Optional<Course> course = courseRepository.findById(id);
            
            if (course.isPresent()) {
                logger.info("Found course with ID: {}", id);
            } else {
                logger.warn("Course not found with ID: {}", id);
            }
            
            return course;
            
        } catch (Exception ex) {
            span.setStatus(StatusCode.ERROR);
            span.recordException(ex);
            logger.error("Error retrieving course by ID: {}", id, ex);
            throw ex;
        } finally {
            span.end();
        }
    }
    
    /**
     * Update an existing course
     */
    public Course updateCourse(Long id, Course updatedCourse) {
        Span span = tracer.spanBuilder("CourseService.updateCourse").startSpan();
        try (var scope = span.makeCurrent()) {
            logger.info("Updating course with ID: {}", id);
            
            Optional<Course> existingCourse = courseRepository.findById(id);
            if (existingCourse.isEmpty()) {
                logger.warn("Course not found for update with ID: {}", id);
                throw new RuntimeException("Course not found with ID: " + id);
            }
            
            Course course = existingCourse.get();
            course.setName(updatedCourse.getName());
            course.setPrice(updatedCourse.getPrice());
            course.setDescription(updatedCourse.getDescription());
            
            Course savedCourse = courseRepository.save(course);
            
            logger.info("Successfully updated course with ID: {}", id);
            return savedCourse;
            
        } catch (Exception ex) {
            span.setStatus(StatusCode.ERROR);
            span.recordException(ex);
            logger.error("Error updating course with ID: {}", id, ex);
            throw ex;
        } finally {
            span.end();
        }
    }
    
    /**
     * Delete course by ID
     */
    public boolean deleteCourse(Long id) {
        Span span = tracer.spanBuilder("CourseService.deleteCourse").startSpan();
        try (var scope = span.makeCurrent()) {
            logger.info("Deleting course with ID: {}", id);
            
            if (!courseRepository.existsById(id)) {
                logger.warn("Course not found for deletion with ID: {}", id);
                return false;
            }
            
            boolean deleted = courseRepository.deleteById(id);
            
            if (deleted) {
                logger.info("Successfully deleted course with ID: {}", id);
            } else {
                logger.warn("Failed to delete course with ID: {}", id);
            }
            
            return deleted;
            
        } catch (Exception ex) {
            span.setStatus(StatusCode.ERROR);
            span.recordException(ex);
            logger.error("Error deleting course with ID: {}", id, ex);
            throw ex;
        } finally {
            span.end();
        }
    }
    
    /**
     * Apply discount to course
     */
    public Course applyCourseDiscount(Long id, BigDecimal discountPercentage) {
        Span span = tracer.spanBuilder("CourseService.applyCourseDiscount").startSpan();
        try (var scope = span.makeCurrent()) {
            logger.info("Applying {}% discount to course with ID: {}", discountPercentage, id);
            
            Optional<Course> courseOpt = courseRepository.findById(id);
            if (courseOpt.isEmpty()) {
                throw new RuntimeException("Course not found with ID: " + id);
            }
            
            Course course = courseOpt.get();
            BigDecimal originalPrice = course.getPrice();
            
            course.applyDiscount(discountPercentage);
            Course savedCourse = courseRepository.save(course);
            
            logger.info("Applied discount to course {}: {} -> {}", 
                       id, originalPrice, savedCourse.getPrice());
            
            return savedCourse;
            
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