package com.example.migration.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Course domain entity
 * Following Clean Architecture and DDD principles
 */
@Entity
@Table(name = "courses", 
       indexes = @Index(name = "idx_course_name", columnList = "name"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Course extends BaseEntity {
    
    @NotBlank(message = "Course name is required")
    @Size(min = 2, max = 100, message = "Course name must be between 2 and 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @NotNull(message = "Course price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Course price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Course price must have at most 2 decimal places")
    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;
    
    @Size(max = 500, message = "Course description must not exceed 500 characters")
    @Column(name = "description", length = 500)
    private String description;
    
    /**
     * Business logic method to check if course is expensive
     */
    public boolean isExpensive() {
        return price != null && price.compareTo(BigDecimal.valueOf(1000)) > 0;
    }
    
    /**
     * Business logic method to apply discount
     */
    public void applyDiscount(BigDecimal discountPercentage) {
        if (discountPercentage != null && 
            discountPercentage.compareTo(BigDecimal.ZERO) > 0 && 
            discountPercentage.compareTo(BigDecimal.valueOf(100)) <= 0) {
            
            BigDecimal discountAmount = price.multiply(discountPercentage)
                                           .divide(BigDecimal.valueOf(100));
            this.price = price.subtract(discountAmount);
        }
    }
}