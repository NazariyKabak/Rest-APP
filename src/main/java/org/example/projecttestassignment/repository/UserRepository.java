package org.example.projecttestassignment.repository;


import org.example.projecttestassignment.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Page<User> findAllByBirthDateBetween(Pageable pageable, LocalDate from, LocalDate to);
}
