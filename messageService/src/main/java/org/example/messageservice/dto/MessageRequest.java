package org.example.messageservice.dto;

import jakarta.validation.constraints.NotBlank;

public record MessageRequest(
        @NotBlank(message = "Content cannot be blank")
        String content
) {
}