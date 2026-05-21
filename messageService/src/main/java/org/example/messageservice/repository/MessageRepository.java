package org.example.messageservice.repository;

import org.example.messageservice.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    List<Message> findBySenderId(UUID senderId);
}