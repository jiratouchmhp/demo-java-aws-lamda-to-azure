package com.example.migration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class MigrationApplicationIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void contextLoads() {
        // Test that the application context loads successfully
    }

    @Test
    void pingEndpoint_shouldReturnPongResponse() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/ping", String.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("pong");
        assertThat(response.getBody()).contains("Hello, World!");
    }

    @Test
    void coursesEndpoint_shouldReturnEmptyListInitially() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/courses", String.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("[]");
    }

    @Test
    void coursesEndpoint_shouldHandleCrudOperations() {
        String baseUrl = "http://localhost:" + port + "/courses";
        
        // Test POST - Create course
        String courseJson = "{\"id\": 1, \"name\": \"Integration Test Course\", \"price\": 79.99}";
        
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        org.springframework.http.HttpEntity<String> request = new org.springframework.http.HttpEntity<>(courseJson, headers);
        
        ResponseEntity<String> postResponse = restTemplate.postForEntity(
                baseUrl, request, String.class);
        
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        
        // Test GET - Retrieve all courses
        ResponseEntity<String> getResponse = restTemplate.getForEntity(baseUrl, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).contains("Integration Test Course");
        
        // Test GET by ID - Retrieve specific course
        ResponseEntity<String> getByIdResponse = restTemplate.getForEntity(
                baseUrl + "/1", String.class);
        assertThat(getByIdResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getByIdResponse.getBody()).contains("Integration Test Course");
    }
}