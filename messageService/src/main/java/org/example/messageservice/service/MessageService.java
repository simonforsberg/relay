package org.example.messageservice.service;

import org.example.messageservice.config.RabbitMQConfig;
import org.example.messageservice.dto.MessageRequest;
import org.example.messageservice.dto.MessageResponse;
import org.example.messageservice.model.Message;
import org.example.messageservice.repository.MessageRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final RabbitTemplate rabbitTemplate;

    public MessageService(MessageRepository messageRepository, RabbitTemplate rabbitTemplate) {
        this.messageRepository = messageRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public MessageResponse createMessage(MessageRequest request, String senderId) {
        Message message = new Message();
        message.setContent(request.content());
        message.setSenderId(senderId);

        Message saved = messageRepository.save(message);

        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, saved.getId().toString());

        return toResponse(saved);
    }

    public List<MessageResponse> findAll() {
        return messageRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<MessageResponse> findBySenderId(UUID senderId) {
        return messageRepository.findBySenderId(senderId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public Optional<MessageResponse> findById(UUID id) {
        return messageRepository.findById(id).map(this::toResponse);
    }

    private MessageResponse toResponse(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getContent(),
                message.getSenderId(),
                message.getCreatedAt()
        );
    }
}