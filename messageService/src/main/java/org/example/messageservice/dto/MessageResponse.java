package org.example.messageservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class MessageResponse {
    private UUID id;
    private String content;
    private UUID senderId;
    private LocalDateTime createdAt;
}