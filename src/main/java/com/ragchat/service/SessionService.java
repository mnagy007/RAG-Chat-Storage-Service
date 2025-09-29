package com.ragchat.service;

import com.ragchat.model.ChatSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface SessionService {

    ChatSession createSession(String userId, String title);

    Page<ChatSession> list(String userId, Boolean fav, Pageable pageable);

    void rename(UUID id, String title);

    void favorite(UUID id, boolean favorite);

    void delete(UUID id);
}
