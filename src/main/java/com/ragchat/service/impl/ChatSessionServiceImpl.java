package com.ragchat.service.impl;

import com.ragchat.exception.NotFoundException;
import com.ragchat.model.ChatSession;
import com.ragchat.repo.ChatSessionRepo;
import com.ragchat.service.SessionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class ChatSessionServiceImpl implements SessionService {

    private final ChatSessionRepo chatSessionRepo;

    ChatSessionServiceImpl(ChatSessionRepo chatSessionRepo) {
        this.chatSessionRepo = chatSessionRepo;
    }

    @Override
    @Transactional
    public ChatSession createSession(String userId, String title) {
        ChatSession s = new ChatSession();
        s.setUserId(userId);
        s.setTitle(title);
        return chatSessionRepo.save(s);
    }
    @Override
    @Transactional
    public void rename(UUID id, String title) {
        ChatSession s = get(id);
        s.setTitle(title);
    }

    @Override
    @Transactional
    public void favorite(UUID id, boolean favorite) {
        ChatSession s = get(id);
        s.setFavorite(favorite);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        ChatSession s = get(id);
        s.setDeletedAt(Instant.now());
    }

    @Override
    public Page<ChatSession> list(String userId, Boolean fav, Pageable pageable) {
        return chatSessionRepo.findAllForUser(userId, fav, pageable);
    }

    private ChatSession get(UUID id) {
        return chatSessionRepo.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("No session found!"));
    }

}
