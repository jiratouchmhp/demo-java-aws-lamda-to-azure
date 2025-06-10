package com.example.migration.adapter;

import com.example.migration.domain.Course;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryCourseRepositoryTest {

    private InMemoryCourseRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryCourseRepository();
    }

    @Test
    void save_AddsCourseToRepository() {
        Course course = new Course(1, "Spring Boot", 99.99);

        repository.save(course);

        List<Course> courses = repository.findAll();
        assertThat(courses).hasSize(1);
        assertThat(courses.get(0)).isEqualTo(course);
    }

    @Test
    void findAll_ReturnsAllCourses() {
        Course course1 = new Course(1, "Spring Boot", 99.99);
        Course course2 = new Course(2, "Java Fundamentals", 79.99);
        repository.save(course1);
        repository.save(course2);

        List<Course> courses = repository.findAll();

        assertThat(courses).hasSize(2);
        assertThat(courses).containsExactly(course1, course2);
    }

    @Test
    void findById_ExistingId_ReturnsCourse() {
        Course course = new Course(1, "Spring Boot", 99.99);
        repository.save(course);

        Optional<Course> foundCourse = repository.findById(1);

        assertThat(foundCourse).isPresent();
        assertThat(foundCourse.get()).isEqualTo(course);
    }

    @Test
    void findById_NonExistingId_ReturnsEmpty() {
        Optional<Course> foundCourse = repository.findById(999);

        assertThat(foundCourse).isEmpty();
    }

    @Test
    void updateById_ExistingId_UpdatesCourse() {
        Course originalCourse = new Course(1, "Spring Boot", 99.99);
        Course updatedCourse = new Course(1, "Advanced Spring Boot", 149.99);
        repository.save(originalCourse);

        boolean result = repository.updateById(1, updatedCourse);

        assertThat(result).isTrue();
        Optional<Course> foundCourse = repository.findById(1);
        assertThat(foundCourse).isPresent();
        assertThat(foundCourse.get().getName()).isEqualTo("Advanced Spring Boot");
        assertThat(foundCourse.get().getPrice()).isEqualTo(149.99);
    }

    @Test
    void updateById_NonExistingId_ReturnsFalse() {
        Course course = new Course(999, "Non-existing Course", 99.99);

        boolean result = repository.updateById(999, course);

        assertThat(result).isFalse();
    }

    @Test
    void deleteById_ExistingId_RemovesCourse() {
        Course course = new Course(1, "Spring Boot", 99.99);
        repository.save(course);

        boolean result = repository.deleteById(1);

        assertThat(result).isTrue();
        assertThat(repository.findAll()).isEmpty();
    }

    @Test
    void deleteById_NonExistingId_ReturnsFalse() {
        boolean result = repository.deleteById(999);

        assertThat(result).isFalse();
    }
}