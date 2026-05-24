package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.UserRequestDTO;
import org.example.dto.UserResponseDTO;
import org.example.entity.User;
import org.example.exception.UserServiceException;
import org.example.mapper.UserMapper;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO requestDTO) {

        log.info("Creating user with email: {}", requestDTO.getEmail());

        if (userRepository.existsByEmail(requestDTO.getEmail())) {
            throw new UserServiceException(
                    "User with email " + requestDTO.getEmail() + " already exists"
            );
        }

        User user = userMapper.toEntity(requestDTO);

        User savedUser = userRepository.save(user);

        log.info("User created with id: {}", savedUser.getId());

        return userMapper.toResponseDTO(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {

        log.info("Fetching user with id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserServiceException("User not found with id: " + id));

        return userMapper.toResponseDTO(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {

        log.info("Fetching all users");

        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public UserResponseDTO updateUser(Long id, UserRequestDTO requestDTO) {

        log.info("Updating user with id: {}", id);

        User existingUser = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserServiceException("User not found with id: " + id));

        if (!existingUser.getEmail().equals(requestDTO.getEmail())
                && userRepository.existsByEmail(requestDTO.getEmail())) {

            throw new UserServiceException(
                    "Email " + requestDTO.getEmail() + " already taken"
            );
        }

        userMapper.updateEntity(existingUser, requestDTO);

        User updatedUser = userRepository.save(existingUser);

        log.info("User updated with id: {}", updatedUser.getId());

        return userMapper.toResponseDTO(updatedUser);
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
}