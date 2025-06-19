package com.example.migration.adapter.web;

import com.example.migration.adapter.web.dto.CourseRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Course Controller Integration Tests
 * Testing the REST API endpoints
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class CourseControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateCourse() throws Exception {
        CourseRequestDto courseRequest = new CourseRequestDto(
            "Spring Boot Fundamentals",
            BigDecimal.valueOf(299.99),
            "Learn Spring Boot from scratch"
        );

        mockMvc.perform(post("/api/v1/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(courseRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Spring Boot Fundamentals"))
                .andExpect(jsonPath("$.price").value(299.99))
                .andExpect(jsonPath("$.description").value("Learn Spring Boot from scratch"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void shouldGetAllCourses() throws Exception {
        mockMvc.perform(get("/api/v1/courses"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldReturn404WhenCourseNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/courses/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Course not found with ID: 999"))
                .andExpect(jsonPath("$.code").value("COURSE_NOT_FOUND"));
    }

    @Test
    void shouldReturn400WhenInvalidCourseData() throws Exception {
        CourseRequestDto invalidCourse = new CourseRequestDto(
            "", // Invalid empty name
            BigDecimal.valueOf(-10), // Invalid negative price
            null
        );

        mockMvc.perform(post("/api/v1/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidCourse)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }
}