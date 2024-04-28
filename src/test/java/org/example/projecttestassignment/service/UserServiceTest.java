package org.example.projecttestassignment.service;


import jakarta.validation.ValidationException;
import org.example.projecttestassignment.dto.UserRequest;
import org.example.projecttestassignment.exception.UserNotFoundException;
import org.example.projecttestassignment.model.User;
import org.example.projecttestassignment.repository.UserRepository;
import org.example.projecttestassignment.services.Impl.UserServiceImpl;
import org.example.projecttestassignment.services.TimeProvider;
import org.example.projecttestassignment.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Field;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@SpringBootTest
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private Clock clock;
    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserRequest userRequest;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        TimeProvider timeProvider = () -> LocalDate.of(2024, 4, 27);
        userService = new UserServiceImpl(userRepository, timeProvider);

        // Use reflection to set the MIN_AGE field
        Field minAgeField = UserServiceImpl.class.getDeclaredField("MIN_AGE");
        minAgeField.setAccessible(true);
        minAgeField.set(userService, 18);

        user = new User(1L, "email@example.com", "John", "Doe", LocalDate.of(2000, 1, 1), "123 Main St", "555-1234");
        userRequest = new UserRequest("email@example.com", "John", "Doe", LocalDate.of(2000, 1, 1), "123 Main St", "555-1234");
    }

    @Test
    void createUser_Success() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        User createdUser = userService.createUser(userRequest);

        assertNotNull(createdUser);
        assertEquals(user.getEmail(), createdUser.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void findUserById_NotFound() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.findUserById(userId), "A UserNotFoundException should be thrown if the user is not found.");
        verify(userRepository).findById(userId);
    }

    @Test
    void updateUser_Success() {
        // Arrange
        Long userId = 1L;
        User updatedUser = new User(userId, "newemail@example.com", "Jane", "Doe", LocalDate.of(1999, 12, 31), "321 New St", "555-4321");
        UserRequest updateRequest = new UserRequest("newemail@example.com", "Jane", "Doe", LocalDate.of(1999, 12, 31), "321 New St", "555-4321");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // Act
        User result = userService.updateUser(userId, updateRequest);

        // Assert
        assertNotNull(result);
        assertEquals(updateRequest.getEmail(), result.getEmail());
        assertEquals(updateRequest.getFirstName(), result.getFirstName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_NotFound() {

        Long userId = 2L; // ID that does not exist
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(userId, userRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void partialUpdateUser_Success() {
        // Arrange
        Long userId = 1L;
        UserRequest partialUpdateRequest = new UserRequest(null, "Jane", null, null, null, null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User result = userService.partialUpdateUser(userId, partialUpdateRequest);

        // Assert
        assertNotNull(result);
        assertEquals(partialUpdateRequest.getFirstName(), result.getFirstName());
        verify(userRepository).save(any(User.class));
    }
    @Test
    void deleteUser_Success() {
        // Arrange
        Long userId = 1L;
        doNothing().when(userRepository).deleteById(userId);
        when(userRepository.existsById(userId)).thenReturn(true);

        // Act
        userService.deleteUser(userId);

        // Assert
        verify(userRepository).deleteById(userId);
    }
    @Test
    void deleteUser_NotFound() {
        // Arrange
        Long userId = 2L; // ID that does not exist
        when(userRepository.existsById(userId)).thenReturn(false);

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));
        verify(userRepository, never()).deleteById(userId);
    }

    @Test
    void searchUsers_ByDateRange_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        LocalDate from = LocalDate.of(1990, 1, 1);
        LocalDate to = LocalDate.of(2000, 1, 1); // Make sure 'to' is after 'from'
        List<User> expectedUsers = List.of(user);

        when(userRepository.findAllByBirthDateBetween(eq(pageable), eq(from), eq(to)))
                .thenReturn(new PageImpl<>(expectedUsers));

        // Act
        List<User> result = userService.searchUsers(pageable, from, to);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(expectedUsers.size(), result.size());
        assertEquals(expectedUsers.get(0).getBirthDate(), result.get(0).getBirthDate());
        verify(userRepository).findAllByBirthDateBetween(eq(pageable), eq(from), eq(to));
    }

    @Test
    void searchUsers_InvalidDateRange() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        LocalDate from = LocalDate.now().plusDays(1); // Future date
        LocalDate to = LocalDate.now().minusDays(1); // Past date

        // Act & Assert
        assertThrows(ValidationException.class, () -> userService.searchUsers(pageable, from, to));
        verify(userRepository, never()).findAllByBirthDateBetween(any(), any(), any());
    }




}
