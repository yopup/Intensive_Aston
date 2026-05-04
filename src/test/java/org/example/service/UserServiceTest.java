package org.example.service;

import org.example.dto.UserRequestDTO;
import org.example.dto.UserResponseDTO;
import org.example.entity.User;
import org.example.exception.UserServiceException;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserRequestDTO testRequest;

    @BeforeEach
    void setUp() {
        testUser = new User("John", "john@test.com", 25);
        testUser.setId(1L);
        testUser.setCreatedAt(LocalDateTime.now());

        testRequest = new UserRequestDTO("John", "john@test.com", 25);
    }

    @Test
    @DisplayName("createUser — should save and return DTO")
    void createUser_shouldSaveAndReturnDto() {
        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponseDTO result = userService.createUser(testRequest);

        assertNotNull(result);
        assertEquals("John", result.getName());
        assertEquals("john@test.com", result.getEmail());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("createUser — should throw when name empty")
    void createUser_emptyName_shouldThrow() {
        testRequest.setName("");
        assertThrows(UserServiceException.class, () -> userService.createUser(testRequest));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("createUser — should throw when email invalid")
    void createUser_invalidEmail_shouldThrow() {
        testRequest.setEmail("invalid");
        assertThrows(UserServiceException.class, () -> userService.createUser(testRequest));
    }

    @Test
    @DisplayName("createUser — should throw when age invalid")
    void createUser_invalidAge_shouldThrow() {
        testRequest.setAge(200);
        assertThrows(UserServiceException.class, () -> userService.createUser(testRequest));
    }

    @Test
    @DisplayName("createUser — should throw when email exists")
    void createUser_emailExists_shouldThrow() {
        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(testUser));
        assertThrows(UserServiceException.class, () -> userService.createUser(testRequest));
    }

    @Test
    @DisplayName("getUserById — should return DTO when exists")
    void getUserById_whenExists_shouldReturnDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserResponseDTO result = userService.getUserById(1L);

        assertEquals("john@test.com", result.getEmail());
    }

    @Test
    @DisplayName("getUserById — should throw when not exists")
    void getUserById_whenNotExists_shouldThrow() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(UserServiceException.class, () -> userService.getUserById(999L));
    }

    @Test
    @DisplayName("getAllUsers — should return list of DTOs")
    void getAllUsers_shouldReturnDtoList() {
        when(userRepository.findAll()).thenReturn(List.of(testUser));

        List<UserResponseDTO> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals("john@test.com", result.get(0).getEmail());
    }

    @Test
    @DisplayName("updateUser — should update only changed email")
    void updateUser_shouldUpdateEmailIfChanged() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmail("new@test.com")).thenReturn(Optional.empty());
        when(userRepository.update(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserRequestDTO updateReq = new UserRequestDTO(null, "new@test.com", null);
        UserResponseDTO updated = userService.updateUser(1L, updateReq);

        assertEquals("new@test.com", updated.getEmail());
        verify(userRepository, times(1)).findByEmail("new@test.com");
    }

    @Test
    @DisplayName("updateUser — should not check email if unchanged")
    void updateUser_shouldNotCheckEmailIfSame() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.update(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserRequestDTO updateReq = new UserRequestDTO(null, "john@test.com", null);
        UserResponseDTO updated = userService.updateUser(1L, updateReq);

        assertEquals("john@test.com", updated.getEmail());
        verify(userRepository, never()).findByEmail("john@test.com");
    }

    @Test
    @DisplayName("deleteUser — should return true when deleted")
    void deleteUser_whenDeleted_shouldReturnTrue() {
        when(userRepository.deleteById(1L)).thenReturn(true);
        assertTrue(userService.deleteUser(1L));
    }
}
