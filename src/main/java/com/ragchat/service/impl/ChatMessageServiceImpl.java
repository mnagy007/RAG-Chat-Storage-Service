package com.ragchat.service.impl;

import com.ragchat.exception.NotFoundException;
import com.ragchat.model.ChatMessage;
import com.ragchat.model.ChatSession;
import com.ragchat.repo.ChatMessageRepo;
import com.ragchat.repo.ChatSessionRepo;
import com.ragchat.service.MessageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ChatMessageServiceImpl implements MessageService {

    private final ChatMessageRepo chatMessageRepo;
    private final ChatSessionRepo chatSessionRepo;

    public ChatMessageServiceImpl(ChatMessageRepo chatMessageRepo, ChatSessionRepo chatSessionRepo) {
        this.chatMessageRepo = chatMessageRepo;
        this.chatSessionRepo = chatSessionRepo;
    }

    @Override
    @Transactional
    public ChatMessage addMessage(UUID sessionId, ChatMessage msg) {
        ChatSession s = chatSessionRepo.findById(sessionId)
                .filter(cs -> cs.getDeletedAt() == null)
                .orElseThrow(() -> new NotFoundException("No session found!"));
        msg.setChatSession(s);
        return chatMessageRepo.save(msg);
    }

    @Override
    public Page<ChatMessage> history(UUID sessionId, Pageable pageable) {
        get(sessionId);
        return chatMessageRepo.findByChatSessionIdOrderByCreatedAtAsc(sessionId, pageable);
    }

    private ChatSession get(UUID id) {
        return chatSessionRepo.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("No session found!"));
    }
}
