package com.ragchat.service;

import com.ragchat.model.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface MessageService {

     ChatMessage addMessage(UUID sessionId, ChatMessage msg);

     Page<ChatMessage> history(UUID sessionId, Pageable pageable);
}
