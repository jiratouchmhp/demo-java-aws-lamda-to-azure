package com.example.migration.application;

import com.example.migration.domain.Course;
import com.example.migration.domain.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Application service for Course use cases.
 * Contains business orchestration logic.
 */
@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public void addCourse(Course course) {
        courseRepository.save(course);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Optional<Course> getCourseById(int id) {
        return courseRepository.findById(id);
    }

    public boolean updateCourse(int id, Course newCourse) {
        return courseRepository.update(id, newCourse);
    }

    public boolean deleteCourse(int id) {
        return courseRepository.deleteById(id);
    }
}