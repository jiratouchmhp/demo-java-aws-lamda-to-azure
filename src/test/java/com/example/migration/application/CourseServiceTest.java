package com.example.migration.application;

import com.example.migration.domain.Course;
import com.example.migration.domain.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    private CourseService courseService;

    @BeforeEach
    void setUp() {
        courseService = new CourseService(courseRepository);
    }

    @Test
    void addCourse_CallsRepositorySave() {
        Course course = new Course(1, "Spring Boot", 99.99);

        courseService.addCourse(course);

        verify(courseRepository).save(course);
    }

    @Test
    void getAllCourses_ReturnsAllCourses() {
        List<Course> expectedCourses = List.of(
                new Course(1, "Spring Boot", 99.99),
                new Course(2, "Java Fundamentals", 79.99)
        );
        when(courseRepository.findAll()).thenReturn(expectedCourses);

        List<Course> actualCourses = courseService.getAllCourses();

        assertThat(actualCourses).isEqualTo(expectedCourses);
    }

    @Test
    void getCourseById_ReturnsOptionalCourse() {
        Course expectedCourse = new Course(1, "Spring Boot", 99.99);
        when(courseRepository.findById(1)).thenReturn(Optional.of(expectedCourse));

        Optional<Course> actualCourse = courseService.getCourseById(1);

        assertThat(actualCourse).isPresent();
        assertThat(actualCourse.get()).isEqualTo(expectedCourse);
    }

    @Test
    void updateCourse_CallsRepositoryUpdateById() {
        Course course = new Course(1, "Updated Course", 129.99);
        when(courseRepository.updateById(1, course)).thenReturn(true);

        boolean result = courseService.updateCourse(1, course);

        assertThat(result).isTrue();
        verify(courseRepository).updateById(1, course);
    }

    @Test
    void deleteCourse_CallsRepositoryDeleteById() {
        when(courseRepository.deleteById(1)).thenReturn(true);

        boolean result = courseService.deleteCourse(1);

        assertThat(result).isTrue();
        verify(courseRepository).deleteById(1);
    }
}