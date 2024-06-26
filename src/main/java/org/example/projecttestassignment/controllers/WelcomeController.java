package org.example.projecttestassignment.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {
    @GetMapping("/")
    public ResponseEntity<String> welcome() {
        return ResponseEntity.ok("Welcome to your application!");
    }
}
