package org.example.projecttestassignment.exception.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.List;
@AllArgsConstructor
@Data
public class ApiError {
    private HttpStatus status;
    private String message;
    private List<String> errors;
}
