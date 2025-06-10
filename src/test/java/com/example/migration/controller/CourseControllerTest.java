package com.example.migration.controller;

import com.example.migration.application.CourseService;
import com.example.migration.domain.Course;
import com.example.migration.dto.CourseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseController.class)
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseService courseService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void addCourse_ValidCourse_ReturnsCreated() throws Exception {
        CourseDto courseDto = new CourseDto(1, "Spring Boot", 99.99);

        mockMvc.perform(post("/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(courseDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Spring Boot"))
                .andExpect(jsonPath("$.price").value(99.99));
    }

    @Test
    void addCourse_InvalidCourse_ReturnsBadRequest() throws Exception {
        CourseDto courseDto = new CourseDto(1, "", -10.0); // Invalid name and price

        mockMvc.perform(post("/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(courseDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllCourses_ReturnsListOfCourses() throws Exception {
        List<Course> courses = List.of(
                new Course(1, "Spring Boot", 99.99),
                new Course(2, "Java Fundamentals", 79.99)
        );
        when(courseService.getAllCourses()).thenReturn(courses);

        mockMvc.perform(get("/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Spring Boot"))
                .andExpect(jsonPath("$[1].name").value("Java Fundamentals"));
    }

    @Test
    void getCourseById_ExistingId_ReturnsCourse() throws Exception {
        Course course = new Course(1, "Spring Boot", 99.99);
        when(courseService.getCourseById(1)).thenReturn(Optional.of(course));

        mockMvc.perform(get("/courses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Spring Boot"))
                .andExpect(jsonPath("$.price").value(99.99));
    }

    @Test
    void getCourseById_NonExistingId_ReturnsNotFound() throws Exception {
        when(courseService.getCourseById(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/courses/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateCourse_ExistingId_ReturnsUpdatedCourse() throws Exception {
        CourseDto courseDto = new CourseDto(1, "Updated Course", 129.99);
        when(courseService.updateCourse(eq(1), any(Course.class))).thenReturn(true);

        mockMvc.perform(put("/courses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(courseDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Course"))
                .andExpect(jsonPath("$.price").value(129.99));
    }

    @Test
    void updateCourse_NonExistingId_ReturnsNotFound() throws Exception {
        CourseDto courseDto = new CourseDto(999, "Non-existing Course", 99.99);
        when(courseService.updateCourse(eq(999), any(Course.class))).thenReturn(false);

        mockMvc.perform(put("/courses/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(courseDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteCourse_ExistingId_ReturnsNoContent() throws Exception {
        when(courseService.deleteCourse(1)).thenReturn(true);

        mockMvc.perform(delete("/courses/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteCourse_NonExistingId_ReturnsNotFound() throws Exception {
        when(courseService.deleteCourse(999)).thenReturn(false);

        mockMvc.perform(delete("/courses/999"))
                .andExpect(status().isNotFound());
    }
}