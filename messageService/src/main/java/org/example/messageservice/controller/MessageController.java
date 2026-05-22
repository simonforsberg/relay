package org.example.messageservice.controller;

import jakarta.validation.Valid;
import org.example.messageservice.dto.MessageRequest;
import org.example.messageservice.dto.MessageResponse;
import org.example.messageservice.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public ResponseEntity<MessageResponse> createMessage(
            @Valid @RequestBody MessageRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        String senderId = jwt.getSubject();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(messageService.createMessage(request, senderId));
    }

    @GetMapping
    public ResponseEntity<List<MessageResponse>> getAllMessages() {
        return ResponseEntity.ok(messageService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageResponse> getMessageById(@PathVariable UUID id) {
        return messageService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/sender/{senderId}")
    public ResponseEntity<List<MessageResponse>> getMessagesBySender(@PathVariable UUID senderId) {
        return ResponseEntity.ok(messageService.findBySenderId(senderId));
    }
}