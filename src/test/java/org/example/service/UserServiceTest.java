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
        testRequest.setEmail("invalid-email");
        assertThrows(UserServiceException.class, () -> userService.createUser(testRequest));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("createUser — should throw when age null")
    void createUser_nullAge_shouldThrow() {
        testRequest.setAge(null);
        assertThrows(UserServiceException.class, () -> userService.createUser(testRequest));
    }

    @Test
    @DisplayName("createUser — should throw when age out of range")
    void createUser_ageOutOfRange_shouldThrow() {
        testRequest.setAge(200);
        assertThrows(UserServiceException.class, () -> userService.createUser(testRequest));
    }

    @Test
    @DisplayName("createUser — should throw when email already exists")
    void createUser_emailExists_shouldThrow() {
        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(testUser));

        assertThrows(UserServiceException.class, () -> userService.createUser(testRequest));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("getUserById — should return DTO when exists")
    void getUserById_whenExists_shouldReturnDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserResponseDTO result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals("john@test.com", result.getEmail());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("getUserById — should throw when not exists")
    void getUserById_whenNotExists_shouldThrow() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UserServiceException.class, () -> userService.getUserById(999L));
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("getAllUsers — should return list of DTOs")
    void getAllUsers_shouldReturnDtoList() {
        List<User> users = List.of(testUser);
        when(userRepository.findAll()).thenReturn(users);

        List<UserResponseDTO> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals("john@test.com", result.get(0).getEmail());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("updateUser — should not check email if unchanged")
    void updateUser_shouldNotCheckEmailIfSame() {
        UserRequestDTO updateReq = new UserRequestDTO(null, "john@test.com", null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.update(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserResponseDTO result = userService.updateUser(1L, updateReq);

        assertEquals("john@test.com", result.getEmail());
        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository, times(1)).update(any(User.class));
    }

    @Test
    @DisplayName("updateUser — should check email if changed")
    void updateUser_shouldCheckEmailIfChanged() {
        UserRequestDTO updateReq = new UserRequestDTO(null, "new@test.com", null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmail("new@test.com")).thenReturn(Optional.empty());
        when(userRepository.update(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserResponseDTO result = userService.updateUser(1L, updateReq);

        assertEquals("new@test.com", result.getEmail());
        verify(userRepository, times(1)).findByEmail("new@test.com");
        verify(userRepository, times(1)).update(any(User.class));
    }

    @Test
    @DisplayName("updateUser — should throw if new email already taken")
    void updateUser_emailAlreadyTaken_shouldThrow() {
        User otherUser = new User("Other", "new@test.com", 30);
        otherUser.setId(2L);

        UserRequestDTO updateReq = new UserRequestDTO(null, "new@test.com", null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmail("new@test.com")).thenReturn(Optional.of(otherUser));

        assertThrows(UserServiceException.class, () -> userService.updateUser(1L, updateReq));
        verify(userRepository, never()).update(any());
    }

    @Test
    @DisplayName("deleteUser — should return true when deleted")
    void deleteUser_whenDeleted_shouldReturnTrue() {
        when(userRepository.deleteById(1L)).thenReturn(true);

        boolean result = userService.deleteUser(1L);

        assertTrue(result);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteUser — should return false when not found")
    void deleteUser_whenNotFound_shouldReturnFalse() {
        when(userRepository.deleteById(999L)).thenReturn(false);

        boolean result = userService.deleteUser(999L);

        assertFalse(result);
        verify(userRepository, times(1)).deleteById(999L);
    }
}

