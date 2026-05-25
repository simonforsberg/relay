package org.example.authservice.dto;

import java.util.UUID;

public record UserAuthResponse(
        UUID id,
        String username,
        String email,
        String passwordHash
) {
}