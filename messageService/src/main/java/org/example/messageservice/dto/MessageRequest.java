package org.example.messageservice.dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class MessageRequest {
    private String content;
    private UUID senderId;
}