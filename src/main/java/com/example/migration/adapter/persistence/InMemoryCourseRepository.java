package com.example.migration.adapter.persistence;

import com.example.migration.domain.Course;
import com.example.migration.domain.CourseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class InMemoryCourseRepository implements CourseRepository {
    
    private final List<Course> courses = new ArrayList<>();

    @Override
    public Course save(Course course) {
        courses.add(course);
        log.debug("Saved course: {}", course);
        return course;
    }

    @Override
    public Optional<Course> findById(int id) {
        return courses.stream()
                .filter(course -> course.getId() == id)
                .findFirst();
    }

    @Override
    public List<Course> findAll() {
        return new ArrayList<>(courses);
    }

    @Override
    public boolean deleteById(int id) {
        boolean removed = courses.removeIf(course -> course.getId() == id);
        if (removed) {
            log.debug("Deleted course with id: {}", id);
        }
        return removed;
    }

    @Override
    public boolean updateCourse(int id, Course newCourse) {
        Optional<Course> existingCourse = findById(id);
        if (existingCourse.isPresent()) {
            courses.remove(existingCourse.get());
            courses.add(newCourse);
            log.debug("Updated course with id: {}", id);
            return true;
        }
        return false;
    }
}