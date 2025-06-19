package com.example.migration.application.service;

import com.example.migration.domain.model.Course;
import com.example.migration.domain.port.CourseRepository;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.Tracer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CourseService
 * Following Clean Architecture testing principles
 */
@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private Tracer tracer;

    @Mock
    private SpanBuilder spanBuilder;
    
    @Mock
    private Span span;

    private CourseService courseService;

    @BeforeEach
    void setUp() {
        // Setup tracer mocks
        when(tracer.spanBuilder(anyString())).thenReturn(spanBuilder);
        when(spanBuilder.startSpan()).thenReturn(span);
        when(span.makeCurrent()).thenReturn(mock(io.opentelemetry.context.Scope.class));
        
        courseService = new CourseService(courseRepository, tracer);
    }

    @Test
    void shouldCreateCourse() {
        // Given
        Course course = new Course();
        course.setName("Spring Boot Fundamentals");
        course.setPrice(BigDecimal.valueOf(299.99));
        course.setDescription("Learn Spring Boot");

        Course savedCourse = new Course();
        savedCourse.setId(1L);
        savedCourse.setName("Spring Boot Fundamentals");
        savedCourse.setPrice(BigDecimal.valueOf(299.99));
        savedCourse.setDescription("Learn Spring Boot");

        when(courseRepository.save(any(Course.class))).thenReturn(savedCourse);

        // When
        Course result = courseService.createCourse(course);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Spring Boot Fundamentals");
        assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(299.99));
        verify(courseRepository).save(course);
    }

    @Test
    void shouldGetAllCourses() {
        // Given
        List<Course> courses = Arrays.asList(
            createCourse(1L, "Course 1", BigDecimal.valueOf(100)),
            createCourse(2L, "Course 2", BigDecimal.valueOf(200))
        );
        when(courseRepository.findAll()).thenReturn(courses);

        // When
        List<Course> result = courseService.getAllCourses();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Course 1");
        assertThat(result.get(1).getName()).isEqualTo("Course 2");
        verify(courseRepository).findAll();
    }

    @Test
    void shouldGetCourseById() {
        // Given
        Long courseId = 1L;
        Course course = createCourse(courseId, "Test Course", BigDecimal.valueOf(150));
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        // When
        Optional<Course> result = courseService.getCourseById(courseId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Test Course");
        verify(courseRepository).findById(courseId);
    }

    @Test
    void shouldReturnEmptyWhenCourseNotFound() {
        // Given
        Long courseId = 999L;
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        // When
        Optional<Course> result = courseService.getCourseById(courseId);

        // Then
        assertThat(result).isEmpty();
        verify(courseRepository).findById(courseId);
    }

    @Test
    void shouldDeleteCourseSuccessfully() {
        // Given
        Long courseId = 1L;
        when(courseRepository.existsById(courseId)).thenReturn(true);
        when(courseRepository.deleteById(courseId)).thenReturn(true);

        // When
        boolean result = courseService.deleteCourse(courseId);

        // Then
        assertThat(result).isTrue();
        verify(courseRepository).existsById(courseId);
        verify(courseRepository).deleteById(courseId);
    }

    @Test
    void shouldReturnFalseWhenDeletingNonExistentCourse() {
        // Given
        Long courseId = 999L;
        when(courseRepository.existsById(courseId)).thenReturn(false);

        // When
        boolean result = courseService.deleteCourse(courseId);

        // Then
        assertThat(result).isFalse();
        verify(courseRepository).existsById(courseId);
        verify(courseRepository, never()).deleteById(courseId);
    }

    @Test
    void shouldApplyDiscountToCourse() {
        // Given
        Long courseId = 1L;
        BigDecimal originalPrice = BigDecimal.valueOf(1000);
        BigDecimal discountPercentage = BigDecimal.valueOf(10); // 10% discount
        BigDecimal expectedPrice = BigDecimal.valueOf(900); // 1000 - (1000 * 0.10)

        Course course = createCourse(courseId, "Expensive Course", originalPrice);
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(courseRepository.save(any(Course.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Course result = courseService.applyCourseDiscount(courseId, discountPercentage);

        // Then
        assertThat(result.getPrice()).isEqualTo(expectedPrice);
        verify(courseRepository).findById(courseId);
        verify(courseRepository).save(course);
    }

    @Test
    void shouldThrowExceptionWhenApplyingDiscountToNonExistentCourse() {
        // Given
        Long courseId = 999L;
        BigDecimal discountPercentage = BigDecimal.valueOf(10);
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> courseService.applyCourseDiscount(courseId, discountPercentage))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Course not found with ID: " + courseId);

        verify(courseRepository).findById(courseId);
        verify(courseRepository, never()).save(any());
    }

    private Course createCourse(Long id, String name, BigDecimal price) {
        Course course = new Course();
        course.setId(id);
        course.setName(name);
        course.setPrice(price);
        course.setDescription("Test description");
        return course;
    }
}