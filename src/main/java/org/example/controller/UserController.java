package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.UserRequestDTO;
import org.example.dto.UserResponseDTO;
import org.example.exception.UserServiceException;
import org.example.service.UserService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User API", description = "CRUD operations for managing users")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "Create a new user", description = "Creates a user and returns the created user with links")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO requestDTO) {
        log.info("POST /api/users - Creating user");
        UserResponseDTO response = userService.createUser(requestDTO);
        addLinks(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Returns a single user with HATEOAS links")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponseDTO> getUserById(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long id) {
        log.info("GET /api/users/{} - Fetching user", id);
        UserResponseDTO response = userService.getUserById(id);
        addLinks(response);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "Returns list of all users with HATEOAS links")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CollectionModel<UserResponseDTO>> getAllUsers() {
        log.info("GET /api/users - Fetching all users");
        List<UserResponseDTO> users = userService.getAllUsers();

        users.forEach(this::addLinks);

        Link selfLink = linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel();
        Link createLink = linkTo(methodOn(UserController.class).createUser(null)).withRel("create");

        CollectionModel<UserResponseDTO> result = CollectionModel.of(users, selfLink, createLink);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Updates an existing user and returns updated user with links")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "Email already taken")
    })
    public ResponseEntity<UserResponseDTO> updateUser(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDTO requestDTO) {
        log.info("PUT /api/users/{} - Updating user", id);
        UserResponseDTO response = userService.updateUser(id, requestDTO);
        addLinks(response);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Deletes a user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long id) {
        log.info("DELETE /api/users/{} - Deleting user", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    private void addLinks(UserResponseDTO user) {
        if (user == null || user.getId() == null) return;

        Link selfLink = linkTo(methodOn(UserController.class).getUserById(user.getId())).withSelfRel();
        user.add(selfLink);

        Link updateLink = linkTo(methodOn(UserController.class).updateUser(user.getId(), null)).withRel("update");
        user.add(updateLink);

        Link deleteLink = linkTo(methodOn(UserController.class).deleteUser(user.getId())).withRel("delete");
        user.add(deleteLink);

        Link allUsersLink = linkTo(methodOn(UserController.class).getAllUsers()).withRel("all-users");
        user.add(allUsersLink);
    }
}
