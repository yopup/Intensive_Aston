package org.example.service;

import org.example.dto.UserRequestDTO;
import org.example.dto.UserResponseDTO;
import org.example.entity.User;
import org.example.exception.UserServiceException;
import org.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO requestDTO) {
        log.info("Creating user with email: {}", requestDTO.getEmail());

        if (userRepository.existsByEmail(requestDTO.getEmail())) {
            log.warn("Email already exists: {}", requestDTO.getEmail());
            throw new UserServiceException("User with email " + requestDTO.getEmail() + " already exists");
        }

        User user = new User();
        user.setName(requestDTO.getName());
        user.setEmail(requestDTO.getEmail());
        user.setAge(requestDTO.getAge());

        User saved = userRepository.save(user);
        log.info("User created with id: {}", saved.getId());

        return convertToResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {
        log.info("Fetching user with id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserServiceException("User not found with id: " + id));

        return convertToResponseDTO(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        log.info("Fetching all users");

        return userRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResponseDTO updateUser(Long id, UserRequestDTO requestDTO) {
        log.info("Updating user with id: {}", id);

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserServiceException("User not found with id: " + id));

        if (!requestDTO.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmail(requestDTO.getEmail())) {
                throw new UserServiceException("Email " + requestDTO.getEmail() + " already taken");
            }
            existingUser.setEmail(requestDTO.getEmail());
        }

        existingUser.setName(requestDTO.getName());
        existingUser.setAge(requestDTO.getAge());

        User updated = userRepository.save(existingUser);
        log.info("User updated with id: {}", updated.getId());

        return convertToResponseDTO(updated);
    }

    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);

        if (!userRepository.existsById(id)) {
            throw new UserServiceException("User not found with id: " + id);
        }

        userRepository.deleteById(id);
        log.info("User deleted with id: {}", id);
    }

    private UserResponseDTO convertToResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAge(),
                user.getCreatedAt()
        );
    }
}

