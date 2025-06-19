package com.example.migration.infrastructure.config;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Application Configuration
 * Following Clean Architecture principles
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.example.migration.adapter.persistence")
@EnableJpaAuditing
public class ApplicationConfig {
    
    /**
     * OpenTelemetry Tracer bean
     */
    @Bean
    public Tracer tracer() {
        return GlobalOpenTelemetry.getTracer("course-management-api");
    }
}