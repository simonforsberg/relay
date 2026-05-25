package org.example.userservice.dto;

import java.util.UUID;

public record UserAuthResponse(
        UUID id,
        String username,
        String email,
        String passwordHash
) {
}