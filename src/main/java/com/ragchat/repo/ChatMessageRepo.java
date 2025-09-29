package com.ragchat.repo;

import com.ragchat.model.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChatMessageRepo extends JpaRepository<ChatMessage, Long> {

    Page<ChatMessage> findByChatSessionIdOrderByCreatedAtAsc(UUID sessionId, Pageable pageable);

}
