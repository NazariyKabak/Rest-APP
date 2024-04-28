package org.example.projecttestassignment.generator;

import com.github.javafaker.Faker;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.example.projecttestassignment.model.User;
import org.example.projecttestassignment.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeneratorUsers {
    private final UserRepository userRepository;
    @PostConstruct
    public void init() {
        generateUsers();
    }

    private void generateUsers() {
        Faker faker = new Faker();
        for (int i = 0; i < 40; i++) {
            User user = new User();
            user.setFirstName(faker.name().firstName());
            user.setLastName(faker.name().lastName());
            user.setEmail(faker.internet().emailAddress());
            user.setPhoneNumber(faker.phoneNumber().phoneNumber());
            user.setBirthDate(LocalDate.ofInstant(faker.date().birthday(18, 65).toInstant(), ZoneId.systemDefault()));
            userRepository.save(user);
        }
        log.info("Generated 40 users successfully.");
    }
}
