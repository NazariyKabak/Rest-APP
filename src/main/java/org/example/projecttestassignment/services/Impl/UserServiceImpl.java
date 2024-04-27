package org.example.projecttestassignment.services.Impl;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.projecttestassignment.dto.UserRequest;
import org.example.projecttestassignment.exception.UserNotFoundException;
import org.example.projecttestassignment.model.User;
import org.example.projecttestassignment.repository.UserRepository;
import org.example.projecttestassignment.services.TimeProvider;
import org.example.projecttestassignment.services.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static java.time.LocalDate.now;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TimeProvider timeProvider;
    @Value("${user.min-age}")
    private Integer MIN_AGE;


    @Override
    public User createUser(UserRequest request) {
        log.info("Creating new user: {}", request);
        if (request.getEmail()==null ||
            request.getFirstName()==null||
            request.getLastName()==null||
            request.getBirthDate()==null){
            throw new IllegalArgumentException("\"Не всі обов'язкові поля були вказані");
        }
        LocalDate currentDate=timeProvider.currentDate();
        if (request.getBirthDate().plusYears(MIN_AGE).isAfter(currentDate)){
            throw new IllegalArgumentException("Користувач повинен бути старше 18 років");
        }
        User user = User.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .birthDate(request.getBirthDate())
                .address(request.getAddress())
                .phoneNumber(request.getPhoneNumber())
                .build();
        return userRepository.save(user);
    }


    @Override
    public User findUserById(Long id) {
        log.info("Finding user by id: {}", id);
        return userRepository.findById(id).orElseThrow(()
                -> new UserNotFoundException("User with id " + id + " not found"));
    }

    @Override
    public User updateUser(Long userId,UserRequest request) {
        log.info("Updating user with id {}: {}", userId, request);
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Користувача з ідентифікатором " + userId + " не знайдено"));
        existingUser.setEmail(request.getEmail());
        existingUser.setFirstName(request.getFirstName());
        existingUser.setLastName(request.getLastName());
        existingUser.setBirthDate(request.getBirthDate());
        existingUser.setAddress(request.getAddress());
        existingUser.setPhoneNumber(request.getPhoneNumber());
        return userRepository.save(existingUser);
    }

    @Override
    public User partialUpdateUser(Long userId,UserRequest request) {
        log.info("Partially updating user with id {}: {}", userId, request);
        User existingUser =userRepository.findById(userId)
                .orElseThrow(()
                        ->new IllegalArgumentException("Користувача з ідентифікатором " + userId + " не знайдено"));
        if (request.getEmail()!=null)existingUser.setEmail(request.getEmail());
        if (request.getFirstName()!=null) existingUser.setFirstName(request.getFirstName());
        if (request.getLastName()!=null) existingUser.setLastName(request.getLastName());
        if (request.getBirthDate()!=null) existingUser.setBirthDate(request.getBirthDate());
        if (request.getAddress()!=null) existingUser.setAddress(request.getAddress());
        if (request.getPhoneNumber()!=null) existingUser.setPhoneNumber(request.getPhoneNumber());
        return userRepository.save(existingUser);
    }
    @Override
    public void deleteUser(Long userId) {
        log.info("Deleting user with id: {}", userId);
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User with id " + userId + " not found");
        }
        userRepository.deleteById(userId);
        log.info("User deleted with id: {}", userId);
    }
    @Override
    public List<User> searchUsers(Pageable pageable, LocalDate from, LocalDate to) {
        log.info("Searching users with date range: from {} to {}", from, to);
        if(Objects.isNull(from) || Objects.isNull(to)) {
            return userRepository.findAll(pageable).getContent();
        }

        if(from.isAfter(to)) {
            throw new ValidationException("Invalid date range: 'from' should be before 'to'.");
        }
        return userRepository.findAllByBirthDateBetween(pageable, from, to).getContent();
    }
}
