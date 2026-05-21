package org.example.userservice.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String email,
        LocalDateTime createdAt
) {
}
