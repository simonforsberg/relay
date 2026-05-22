package org.example.messageservice.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record MessageResponse(
        UUID id,
        String content,
        String senderId,
        LocalDateTime createdAt
) {
}