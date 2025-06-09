package com.example.migration.application;

import com.example.migration.domain.Course;
import com.example.migration.domain.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {
    
    private final CourseRepository courseRepository;

    public Course createCourse(Course course) {
        log.info("Creating course with id: {}", course.getId());
        return courseRepository.save(course);
    }

    public List<Course> getAllCourses() {
        log.info("Retrieving all courses");
        return courseRepository.findAll();
    }

    public Optional<Course> getCourseById(int id) {
        log.info("Retrieving course with id: {}", id);
        return courseRepository.findById(id);
    }

    public boolean updateCourse(int id, Course course) {
        log.info("Updating course with id: {}", id);
        return courseRepository.updateCourse(id, course);
    }

    public boolean deleteCourse(int id) {
        log.info("Deleting course with id: {}", id);
        return courseRepository.deleteById(id);
    }
}