package org.example;

import org.example.dto.UserRequestDTO;
import org.example.dto.UserResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class UserServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {

        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("Full CRUD flow should work")
    void fullCrudFlow_shouldWork() {

        UserRequestDTO createRequest = new UserRequestDTO();
        createRequest.setName("Integration Test");
        createRequest.setEmail("integration@test.com");
        createRequest.setAge(30);

        ResponseEntity<UserResponseDTO> createResponse =
                restTemplate.postForEntity(
                        "/api/users",
                        createRequest,
                        UserResponseDTO.class
                );

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());

        Long userId = createResponse.getBody().getId();

        ResponseEntity<UserResponseDTO> getResponse =
                restTemplate.getForEntity(
                        "/api/users/" + userId,
                        UserResponseDTO.class
                );

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals("integration@test.com", getResponse.getBody().getEmail());

        createRequest.setName("Updated Name");

        HttpEntity<UserRequestDTO> requestEntity =
                new HttpEntity<>(createRequest);

        ResponseEntity<UserResponseDTO> updateResponse =
                restTemplate.exchange(
                        "/api/users/" + userId,
                        HttpMethod.PUT,
                        requestEntity,
                        UserResponseDTO.class
                );

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertEquals("Updated Name", updateResponse.getBody().getName());

        restTemplate.delete("/api/users/" + userId);

        ResponseEntity<String> afterDelete =
                restTemplate.getForEntity(
                        "/api/users/" + userId,
                        String.class
                );

        assertEquals(HttpStatus.CONFLICT, afterDelete.getStatusCode());
    }
}
