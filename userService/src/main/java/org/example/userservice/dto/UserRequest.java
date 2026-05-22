package org.example.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(
        @NotBlank(message = "Username cannot be blank")
        @Size(min = 2, max = 50, message = "Username must be between 2 and 50 characters")
        String username,

        @NotBlank(message = "Password cannot be blank")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password,

        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Email must be valid")
        String email
) {
}