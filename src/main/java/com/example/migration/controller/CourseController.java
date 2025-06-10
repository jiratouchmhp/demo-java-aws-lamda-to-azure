package com.example.migration.controller;

import com.example.migration.application.CourseService;
import com.example.migration.domain.Course;
import com.example.migration.dto.CourseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for course management operations
 */
@RestController
@RequestMapping("/courses")
@Tag(name = "Course Management", description = "Operations for managing courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
    @Operation(
        summary = "Create a new course",
        description = "Creates a new course with the provided information"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Course created successfully",
                content = @Content(schema = @Schema(implementation = CourseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<CourseDto> addCourse(@Valid @RequestBody CourseDto courseDto) {
        Course course = mapToDomain(courseDto);
        courseService.addCourse(course);
        return new ResponseEntity<>(courseDto, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(
        summary = "Get all courses",
        description = "Retrieves a list of all available courses"
    )
    @ApiResponse(responseCode = "200", description = "List of courses retrieved successfully")
    public ResponseEntity<List<CourseDto>> getAllCourses() {
        List<Course> courses = courseService.getAllCourses();
        List<CourseDto> courseDtos = courses.stream()
                .map(this::mapToDto)
                .toList();
        return ResponseEntity.ok(courseDtos);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get course by ID",
        description = "Retrieves a specific course by its ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Course found",
                content = @Content(schema = @Schema(implementation = CourseDto.class))),
        @ApiResponse(responseCode = "404", description = "Course not found")
    })
    public ResponseEntity<CourseDto> getCourseById(
            @Parameter(description = "ID of the course to retrieve")
            @PathVariable int id) {
        Optional<Course> course = courseService.getCourseById(id);
        return course.map(c -> ResponseEntity.ok(mapToDto(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update course",
        description = "Updates an existing course with new information"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Course updated successfully",
                content = @Content(schema = @Schema(implementation = CourseDto.class))),
        @ApiResponse(responseCode = "404", description = "Course not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<CourseDto> updateCourse(
            @Parameter(description = "ID of the course to update")
            @PathVariable int id,
            @Valid @RequestBody CourseDto courseDto) {
        Course course = mapToDomain(courseDto);
        boolean updated = courseService.updateCourse(id, course);
        if (updated) {
            return ResponseEntity.ok(courseDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete course",
        description = "Deletes a course by its ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Course deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Course not found")
    })
    public ResponseEntity<Void> deleteCourse(
            @Parameter(description = "ID of the course to delete")
            @PathVariable int id) {
        boolean deleted = courseService.deleteCourse(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Mapping methods to convert between domain and DTO
    private CourseDto mapToDto(Course course) {
        return new CourseDto(course.getId(), course.getName(), course.getPrice());
    }

    private Course mapToDomain(CourseDto dto) {
        return new Course(dto.getId(), dto.getName(), dto.getPrice());
    }
}