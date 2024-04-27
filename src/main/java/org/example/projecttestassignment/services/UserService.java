package org.example.projecttestassignment.services;

import org.example.projecttestassignment.dto.UserRequest;
import org.example.projecttestassignment.model.User;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface UserService {
    User createUser(UserRequest user);
    User findUserById(Long id);
    User updateUser(Long userId, UserRequest user);
    User partialUpdateUser(Long userId, UserRequest partialUser);
    void deleteUser(Long userId);
    List<User>searchUsers(Pageable pageable, LocalDate from, LocalDate to);
}
