package org.example;

import org.example.dto.UserRequestDTO;
import org.example.dto.UserResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("Full CRUD flow should work")
    void fullCrudFlow_shouldWork() {
        // 1. Create user
        UserRequestDTO createRequest = new UserRequestDTO();
        createRequest.setName("Integration Test");
        createRequest.setEmail("integration@test.com");
        createRequest.setAge(30);

        ResponseEntity<UserResponseDTO> createResponse = restTemplate
                .postForEntity("/api/users", createRequest, UserResponseDTO.class);

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody().getId());

        Long userId = createResponse.getBody().getId();

        // 2. Get user
        ResponseEntity<UserResponseDTO> getResponse = restTemplate
                .getForEntity("/api/users/" + userId, UserResponseDTO.class);

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals("integration@test.com", getResponse.getBody().getEmail());

        // 3. Update user
        createRequest.setName("Updated Name");
        restTemplate.put("/api/users/" + userId, createRequest);

        // 4. Delete user
        restTemplate.delete("/api/users/" + userId);

        // 5. Verify deleted
        ResponseEntity<UserResponseDTO> afterDelete = restTemplate
                .getForEntity("/api/users/" + userId, UserResponseDTO.class);

        assertEquals(HttpStatus.CONFLICT, afterDelete.getStatusCode());
    }
}

