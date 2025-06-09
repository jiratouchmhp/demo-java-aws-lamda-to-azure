package com.example.migration.controller;

import com.example.migration.application.CourseService;
import com.example.migration.domain.Course;
import com.example.migration.dto.CourseRequest;
import com.example.migration.dto.CourseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/courses")
@Tag(name = "Course Management", description = "CRUD operations for courses")
@RequiredArgsConstructor
@Slf4j
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    @Operation(summary = "Create a new course", description = "Creates a new course with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Course created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<CourseResponse> createCourse(@Valid @RequestBody CourseRequest request) {
        log.info("Creating course: {}", request.getName());
        Course createdCourse = courseService.createCourse(request.toEntity());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CourseResponse.from(createdCourse));
    }

    @GetMapping
    @Operation(summary = "Get all courses", description = "Retrieves a list of all courses")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all courses")
    public ResponseEntity<List<CourseResponse>> getAllCourses() {
        log.info("Retrieving all courses");
        List<Course> courses = courseService.getAllCourses();
        List<CourseResponse> responses = courses.stream()
                .map(CourseResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get course by ID", description = "Retrieves a specific course by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Course found"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    public ResponseEntity<CourseResponse> getCourseById(
            @Parameter(description = "Course ID") @PathVariable int id) {
        log.info("Retrieving course with id: {}", id);
        Optional<Course> course = courseService.getCourseById(id);
        return course.map(c -> ResponseEntity.ok(CourseResponse.from(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update course", description = "Updates an existing course")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Course updated successfully"),
            @ApiResponse(responseCode = "404", description = "Course not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<CourseResponse> updateCourse(
            @Parameter(description = "Course ID") @PathVariable int id,
            @Valid @RequestBody CourseRequest request) {
        log.info("Updating course with id: {}", id);
        boolean updated = courseService.updateCourse(id, request.toEntity());
        if (updated) {
            return ResponseEntity.ok(CourseResponse.from(request.toEntity()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete course", description = "Deletes a course by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Course deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    public ResponseEntity<Void> deleteCourse(
            @Parameter(description = "Course ID") @PathVariable int id) {
        log.info("Deleting course with id: {}", id);
        boolean deleted = courseService.deleteCourse(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}