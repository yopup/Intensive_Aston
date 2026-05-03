package org.example.service;

import org.example.dto.UserRequestDTO;
import org.example.dto.UserResponseDTO;
import org.example.entity.User;
import org.example.exception.UserServiceException;
import org.example.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

public class UserService {
    private static final Logger logger = LogManager.getLogger(UserService.class);
    private final UserRepository userRepository;

    public UserService() {
        this.userRepository = new UserRepository();
    }

    public UserResponseDTO createUser(UserRequestDTO requestDTO) {
        logger.info("Creating user with email: {}", requestDTO.getEmail());

        if (requestDTO.getName() == null || requestDTO.getName().trim().isEmpty()) {
            throw new UserServiceException("Name cannot be empty");
        }
        if (requestDTO.getEmail() == null || !requestDTO.getEmail().matches("^[^@]+@[^@]+\\.[^@]+$")) {
            throw new UserServiceException("Invalid email format");
        }
        if (requestDTO.getAge() == null || requestDTO.getAge() < 0 || requestDTO.getAge() > 150) {
            throw new UserServiceException("Age must be between 0 and 150");
        }

        if (userRepository.findByEmail(requestDTO.getEmail()).isPresent()) {
            throw new UserServiceException("User with email already exists");
        }

        User user = new User(requestDTO.getName(), requestDTO.getEmail(), requestDTO.getAge());
        User saved = userRepository.save(user);

        return convertToResponseDTO(saved);
    }

    public UserResponseDTO getUserById(Long id) {
        logger.info("Fetching user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserServiceException("User not found with id: " + id));
        return convertToResponseDTO(user);
    }

    public List<UserResponseDTO> getAllUsers() {
        logger.info("Fetching all users");
        return userRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public UserResponseDTO updateUser(Long id, UserRequestDTO requestDTO) {
        logger.info("Updating user with id: {}", id);

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserServiceException("User not found with id: " + id));

        if (requestDTO.getName() != null && !requestDTO.getName().trim().isEmpty()) {
            existingUser.setName(requestDTO.getName());
        }

        if (requestDTO.getEmail() != null && requestDTO.getEmail().matches("^[^@]+@[^@]+\\.[^@]+$")) {
            userRepository.findByEmail(requestDTO.getEmail())
                    .ifPresent(user -> {
                        if (!user.getId().equals(id)) {
                            throw new UserServiceException("Email " + requestDTO.getEmail() + " already taken");
                        }
                    });
            existingUser.setEmail(requestDTO.getEmail());
        }

        if (requestDTO.getAge() != null && requestDTO.getAge() >= 0 && requestDTO.getAge() <= 150) {
            existingUser.setAge(requestDTO.getAge());
        }

        User updated = userRepository.update(existingUser);
        return convertToResponseDTO(updated);
    }

    public boolean deleteUser(Long id) {
        logger.info("Deleting user with id: {}", id);
        return userRepository.deleteById(id);
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
