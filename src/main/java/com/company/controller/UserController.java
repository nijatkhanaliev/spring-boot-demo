package com.company.controller;

import com.company.models.dto.request.UserRequest;
import com.company.models.dto.response.UserResponse;
import com.company.service.UserService;
import com.company.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("v1/users")
@RequiredArgsConstructor
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        logger.info("GET /users called");
        final List<UserResponse> users = userService.getAllUsers();
        logger.info("GET /users returned {} users", users.size());

        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        logger.info("GET /users/{} called", id);
        final UserResponse user = userService.getUserById(id);
        logger.info("User found with ID {}: {}", id, user);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        logger.info("POST /users called with request {}", request);
        final UserResponse user = userService.createUser(request);
        logger.info("User created with ID {}: {}", user.getId(), user);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
                                                   @Valid @RequestBody UserRequest request) {
        logger.info("PUT users/{} called with request {}", id, request);
        final UserResponse user = userService.updateUser(id, request);
        logger.info("User updated with ID {}: {}", id, user);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponse> patchUser(
            @PathVariable Long id,
            @RequestBody Map<String, Object> patchRequest) {
        logger.info("PATCH /users/{} called with fields: {}", id, patchRequest.keySet());
        final UserResponse user = userService.patchUser(id, patchRequest);
        logger.info("User patched with ID {}: {}", id, user);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        logger.info("DELETE /users/{} called", id);
        userService.deleteUser(id);
        logger.info("User deleted with ID {}", id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
