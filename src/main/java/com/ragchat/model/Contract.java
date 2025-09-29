package com.ragchat.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * This class holds the requests and response DTOs for the controllers.
 */
public class Contract {

    public record CreateMsgReq(@NotNull ChatMessage.Sender sender,
                               @NotBlank String content,
                               Map<String,Object> context,
                               Map<String,Object> metadata) {}

    public record MsgRes(Long id, String sender, String content, Instant createdAt,
                         Map<String,Object> context, Map<String,Object> metadata) {
        public static MsgRes of(ChatMessage m){
            return new MsgRes(m.getId(), m.getSender().name(), m.getContent(), m.getCreatedAt(), m.getContext(),
                    m.getMetadata());
        }
    }

    public record CreateSessionReq(@NotBlank String userId, String title) {}
    public record SessionRes(UUID id, String userId, String title, boolean favorite, Instant createdAt) {
        public static SessionRes of(ChatSession s){
            return new SessionRes(s.getId(), s.getUserId(), s.getTitle(), s.isFavorite(), s.getCreatedAt());
        }
    }
}
