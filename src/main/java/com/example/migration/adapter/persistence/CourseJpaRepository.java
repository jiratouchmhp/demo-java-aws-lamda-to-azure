package com.example.migration.adapter.persistence;

import com.example.migration.domain.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * JPA Repository interface for Course entity
 * Spring Data JPA implementation
 */
@Repository
public interface CourseJpaRepository extends JpaRepository<Course, Long> {
    
    /**
     * Find courses by name containing (case-insensitive)
     */
    List<Course> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find courses by price range
     */
    List<Course> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    /**
     * Find courses that are expensive (custom query)
     */
    @Query("SELECT c FROM Course c WHERE c.price > :threshold")
    List<Course> findExpensiveCourses(@Param("threshold") BigDecimal threshold);
    
    /**
     * Count courses by price range
     */
    @Query("SELECT COUNT(c) FROM Course c WHERE c.price BETWEEN :minPrice AND :maxPrice")
    long countByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);
}