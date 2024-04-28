package org.example.projecttestassignment.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.projecttestassignment.dto.ExceptionResponse;
import org.example.projecttestassignment.dto.UserRequest;
import org.example.projecttestassignment.exception.UserNotFoundException;
import org.example.projecttestassignment.model.User;
import org.example.projecttestassignment.services.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;




    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody UserRequest userRequest) {
        log.info("Received request to create user: {}", userRequest);
        User savedUser = userService.createUser(userRequest);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedUser.getId())
                .toUri();
        log.info("User created successfully: {}", savedUser);
        return ResponseEntity.created(location).body(savedUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        log.info("Received request to retrieve user with ID: {}", id);
        User user = userService.findUserById(id);
        log.info("User retrieved successfully: {}", user);
        return ResponseEntity.ok(user);
    }
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Pageable pageable) {
        log.info("Received request to search users with date range: from {} to {}", from, to);
        List<User> users = userService.searchUsers(pageable, from, to);
        log.info("Users found: {}", users);
        return ResponseEntity.ok(users);

    }
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequest userRequest) {
        log.info("Received request to update user with ID {}: {}", id, userRequest);
        User updateUser=userService.updateUser(id, userRequest);
        log.info("User updated successfully: {}", updateUser);
        return ResponseEntity.ok(updateUser);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<User> patchUser(@PathVariable Long id, @RequestBody UserRequest userRequest) {
        log.info("Received request to partially update user with ID {}: {}", id, userRequest);
        User updateUser=userService.partialUpdateUser(id, userRequest);
        log.info("User partially updated successfully: {}", updateUser);
        return ResponseEntity.ok(updateUser);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable Long id) {
        log.info("Received request to delete user with ID: {}", id);
        userService.deleteUser(id);
        log.info("User deleted successfully");
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleUserNotFound(UserNotFoundException ex) {
        ExceptionResponse response = new ExceptionResponse(LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }


}
