package org.example.controller;

import org.example.dto.UserRequestDTO;
import org.example.dto.UserResponseDTO;
import org.example.exception.UserServiceException;
import org.example.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserRequestDTO requestDTO;
    private UserResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new UserRequestDTO("John", "john@test.com", 25);
        responseDTO = new UserResponseDTO(1L, "John", "john@test.com", 25, LocalDateTime.now());
    }

    @Test
    @DisplayName("createUser — should return DTO on success")
    void createUser_shouldReturnDto() {
        when(userService.createUser(requestDTO)).thenReturn(responseDTO);

        UserResponseDTO result = userController.createUser(requestDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userService, times(1)).createUser(requestDTO);
    }

    @Test
    @DisplayName("createUser — should rethrow exception from service")
    void createUser_shouldRethrowServiceException() {
        when(userService.createUser(requestDTO)).thenThrow(new UserServiceException("Email exists"));

        assertThrows(UserServiceException.class, () -> userController.createUser(requestDTO));
    }

    @Test
    @DisplayName("getUser — should return DTO")
    void getUser_shouldReturnDto() {
        when(userService.getUserById(1L)).thenReturn(responseDTO);

        UserResponseDTO result = userController.getUser(1L);

        assertEquals("john@test.com", result.getEmail());
    }

    @Test
    @DisplayName("getUser — should throw if service throws")
    void getUser_shouldThrowIfNotFound() {
        when(userService.getUserById(99L)).thenThrow(new UserServiceException("Not found"));

        assertThrows(UserServiceException.class, () -> userController.getUser(99L));
    }

    @Test
    @DisplayName("getAllUsers — should return list")
    void getAllUsers_shouldReturnList() {
        when(userService.getAllUsers()).thenReturn(List.of(responseDTO));

        List<UserResponseDTO> result = userController.getAllUsers();

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("updateUser — should return updated DTO")
    void updateUser_shouldReturnUpdatedDto() {
        when(userService.updateUser(eq(1L), any(UserRequestDTO.class))).thenReturn(responseDTO);

        UserResponseDTO result = userController.updateUser(1L, requestDTO);

        assertNotNull(result);
        verify(userService).updateUser(1L, requestDTO);
    }

    @Test
    @DisplayName("deleteUser — should return true")
    void deleteUser_shouldReturnTrue() {
        when(userService.deleteUser(1L)).thenReturn(true);

        boolean result = userController.deleteUser(1L);

        assertTrue(result);
    }

    @Test
    @DisplayName("deleteUser — should rethrow exception")
    void deleteUser_shouldRethrow() {
        when(userService.deleteUser(99L)).thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> userController.deleteUser(99L));
    }
}
