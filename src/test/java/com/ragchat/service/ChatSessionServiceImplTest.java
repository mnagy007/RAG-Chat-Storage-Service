package com.ragchat.service;

import com.ragchat.exception.NotFoundException;
import com.ragchat.model.ChatSession;
import com.ragchat.repo.ChatMessageRepo;
import com.ragchat.repo.ChatSessionRepo;
import com.ragchat.service.impl.ChatMessageServiceImpl;
import com.ragchat.model.ChatMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ChatSessionServiceImplTest {

    @Mock
    ChatMessageRepo chatMessageRepo;
    @Mock
    ChatSessionRepo chatSessionRepo;

    @InjectMocks
    ChatMessageServiceImpl service;

    private UUID sessionId;
    private ChatSession session;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        sessionId = UUID.randomUUID();
        session = new ChatSession();
        session.setId(sessionId);
        session.setUserId("u1");
        session.setCreatedAt(Instant.now());

        pageable = PageRequest.of(0, 10, Sort.by("createdAt").ascending());
    }

    // ---------- addMessage ----------

    @Test
    void addMessage_whenSessionExistsAndNotDeleted_setsSessionAndSaves() {
        // Arrange
        when(chatSessionRepo.findById(sessionId)).thenReturn(Optional.of(session));

        ChatMessage toSave = new ChatMessage();
        toSave.setSender(ChatMessage.Sender.USER);
        toSave.setContent("hello");

        ChatMessage saved = new ChatMessage();
        saved.setId(1L);
        saved.setChatSession(session);
        saved.setSender(ChatMessage.Sender.USER);
        saved.setContent("hello");
        when(chatMessageRepo.save(any(ChatMessage.class))).thenReturn(saved);

        ChatMessage result = service.addMessage(sessionId, toSave);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(sessionId, result.getChatSession().getId());
        assertEquals("hello", result.getContent());

        ArgumentCaptor<ChatMessage> captor = ArgumentCaptor.forClass(ChatMessage.class);
        verify(chatMessageRepo).save(captor.capture());
        assertEquals(sessionId, captor.getValue().getChatSession().getId());

        verify(chatSessionRepo).findById(sessionId);
        verifyNoMoreInteractions(chatSessionRepo);
    }

    @Test
    void addMessage_whenSessionSoftDeleted_throwsNotFound() {
        session.setDeletedAt(Instant.now());
        when(chatSessionRepo.findById(sessionId)).thenReturn(Optional.of(session));

        assertThrows(NotFoundException.class,
                () -> service.addMessage(sessionId, new ChatMessage()));

        verify(chatMessageRepo, never()).save(any());
    }

    @Test
    void addMessage_whenSessionMissing_throwsNotFound() {
        when(chatSessionRepo.findById(sessionId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.addMessage(sessionId, new ChatMessage()));

        verify(chatMessageRepo, never()).save(any());
    }

    // ---------- history ----------

    @Test
    void history_whenSessionExists_returnsPageFromRepo() {
        when(chatSessionRepo.findByIdAndDeletedAtIsNull(sessionId)).thenReturn(Optional.of(session));

        ChatMessage m1 = new ChatMessage(); m1.setId(1L); m1.setChatSession(session);
        ChatMessage m2 = new ChatMessage(); m2.setId(2L); m2.setChatSession(session);
        Page<ChatMessage> page = new PageImpl<>(List.of(m1, m2), pageable, 2);

        when(chatMessageRepo.findByChatSessionIdOrderByCreatedAtAsc(sessionId, pageable))
                .thenReturn(page);

        Page<ChatMessage> result = service.history(sessionId, pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals(1L, result.getContent().get(0).getId());

        verify(chatSessionRepo).findByIdAndDeletedAtIsNull(sessionId);
        verify(chatMessageRepo).findByChatSessionIdOrderByCreatedAtAsc(sessionId, pageable);
    }

    @Test
    void history_whenSessionMissing_throwsNotFound_andDoesNotHitMessageRepo() {
        when(chatSessionRepo.findByIdAndDeletedAtIsNull(sessionId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.history(sessionId, pageable));

        verify(chatMessageRepo, never()).findByChatSessionIdOrderByCreatedAtAsc(any(), any());
    }
}
