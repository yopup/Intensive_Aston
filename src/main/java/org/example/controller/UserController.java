package org.example.controller;

import org.example.dto.UserRequestDTO;
import org.example.dto.UserResponseDTO;
import org.example.exception.UserServiceException;
import org.example.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class UserController {
    private static final Logger logger = LogManager.getLogger(UserController.class);
    private final UserService userService;

    public UserController() {
        this.userService = new UserService();
    }

    public UserResponseDTO createUser(UserRequestDTO requestDTO) {
        try {
            logger.info("Controller: Processing create user request for email: {}", requestDTO.getEmail());
            return userService.createUser(requestDTO);
        } catch (IllegalArgumentException e) {
            logger.error("Validation error: {}", e.getMessage());
            throw e;
        } catch (UserServiceException e) {
            logger.error("Controller: {}", e.getMessage());
            throw e;
        }
    }

    public UserResponseDTO getUser(Long id) {
        try {
            logger.info("Controller: Processing get user request for id: {}", id);
            return userService.getUserById(id);
        } catch (Exception e) {
            logger.error("Controller: Error getting user: {}", e.getMessage());
            throw e;
        }
    }

    public List<UserResponseDTO> getAllUsers() {
        try {
            logger.info("Controller: Processing get all users request");
            return userService.getAllUsers();
        } catch (Exception e) {
            logger.error("Controller: Error getting all users: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch users", e);
        }
    }

    public UserResponseDTO updateUser(Long id, UserRequestDTO requestDTO) {
        try {
            logger.info("Controller: Processing update user request for id: {}", id);
            return userService.updateUser(id, requestDTO);
        } catch (Exception e) {
            logger.error("Controller: Error updating user: {}", e.getMessage());
            throw e;
        }
    }

    public boolean deleteUser(Long id) {
        try {
            logger.info("Controller: Processing delete user request for id: {}", id);
            return userService.deleteUser(id);
        } catch (Exception e) {
            logger.error("Controller: Error deleting user: {}", e.getMessage());
            throw new RuntimeException("Failed to delete user", e);
        }
    }
}
