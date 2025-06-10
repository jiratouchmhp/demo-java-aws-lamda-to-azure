package com.example.migration.adapter;

import com.example.migration.domain.Course;
import com.example.migration.domain.CourseRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * In-memory implementation of CourseRepository
 * This is an adapter that implements the domain port
 */
@Repository
public class InMemoryCourseRepository implements CourseRepository {
    
    private final List<Course> courses = new ArrayList<>();
    
    @Override
    public void save(Course course) {
        courses.add(course);
    }
    
    @Override
    public List<Course> findAll() {
        return new ArrayList<>(courses);
    }
    
    @Override
    public Optional<Course> findById(int id) {
        return courses.stream()
                .filter(course -> course.getId() == id)
                .findFirst();
    }
    
    @Override
    public boolean updateById(int id, Course newCourse) {
        return findById(id).map(existingCourse -> {
            courses.remove(existingCourse);
            courses.add(newCourse);
            return true;
        }).orElse(false);
    }
    
    @Override
    public boolean deleteById(int id) {
        return courses.removeIf(course -> course.getId() == id);
    }
}