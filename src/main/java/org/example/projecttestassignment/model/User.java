package org.example.projecttestassignment.model;

import jakarta.persistence.*;

import jakarta.validation.constraints.Email;
import lombok.*;


import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    private Long id;
    @Column(unique = true, nullable = false, length = 100)
    @Email
    private String email;
    @Column(nullable = false, length = 100)
    private String firstName;
    @Column(nullable = false, length = 100)
    private String lastName;
    @Column(nullable = false, length = 100)
    private LocalDate birthDate;
    @Column(length = 100)
    private String address;
    @Column(length = 100)
    private String phoneNumber;

}
