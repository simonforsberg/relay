package org.example.messageservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MessageRequest(
        @NotBlank(message = "Content cannot be blank")
        String content,
        @NotNull(message = "Sender ID cannot be null")
        UUID senderId
) {
}