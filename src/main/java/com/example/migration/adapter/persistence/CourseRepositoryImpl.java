package com.example.migration.adapter.persistence;

import com.example.migration.domain.model.Course;
import com.example.migration.domain.port.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Course Repository Implementation
 * Adapter that implements domain port using JPA repository
 */
@Component
public class CourseRepositoryImpl implements CourseRepository {
    
    private final CourseJpaRepository jpaRepository;
    
    @Autowired
    public CourseRepositoryImpl(CourseJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    @Override
    public Course save(Course course) {
        return jpaRepository.save(course);
    }
    
    @Override
    public List<Course> findAll() {
        return jpaRepository.findAll();
    }
    
    @Override
    public Optional<Course> findById(Long id) {
        return jpaRepository.findById(id);
    }
    
    @Override
    public boolean deleteById(Long id) {
        if (jpaRepository.existsById(id)) {
            jpaRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }
    
    @Override
    public List<Course> findByNameContainingIgnoreCase(String name) {
        return jpaRepository.findByNameContainingIgnoreCase(name);
    }
    
    @Override
    public List<Course> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return jpaRepository.findByPriceBetween(minPrice, maxPrice);
    }
    
    @Override
    public long count() {
        return jpaRepository.count();
    }
}