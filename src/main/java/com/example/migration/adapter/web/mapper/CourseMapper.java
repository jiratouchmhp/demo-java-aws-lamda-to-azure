package com.example.migration.adapter.web.mapper;

import com.example.migration.adapter.web.dto.CourseRequestDto;
import com.example.migration.adapter.web.dto.CourseResponseDto;
import com.example.migration.domain.model.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * MapStruct mapper for Course entity and DTOs
 * Following Clean Architecture - adapter layer mapping
 */
@Mapper(componentModel = "spring")
public interface CourseMapper {
    
    /**
     * Convert Course entity to response DTO
     */
    @Mapping(target = "expensive", expression = "java(course.isExpensive())")
    CourseResponseDto toResponseDto(Course course);
    
    /**
     * Convert list of Course entities to response DTOs
     */
    List<CourseResponseDto> toResponseDtos(List<Course> courses);
    
    /**
     * Convert request DTO to Course entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Course toEntity(CourseRequestDto requestDto);
    
    /**
     * Update existing Course entity from request DTO
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntity(CourseRequestDto requestDto, @MappingTarget Course course);
}