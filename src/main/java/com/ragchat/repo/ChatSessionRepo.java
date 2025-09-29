package com.ragchat.repo;

import com.ragchat.model.ChatSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ChatSessionRepo extends JpaRepository<ChatSession, UUID> {

  @Query("""
  select s from ChatSession s
   where s.userId = :user
     and s.deletedAt is null
     and (:fav is null or s.favorite = :fav)
   order by s.createdAt desc""")
    Page<ChatSession> findAllForUser(
            @Param("user") String user,
            @Param("fav") Boolean fav,
            Pageable pageable);

  Optional<ChatSession> findByIdAndDeletedAtIsNull(UUID id);
}

