package com.example.chatappzalo.repositories;

// Entity for Media

import com.example.chatappzalo.entity.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MediaRepository extends JpaRepository<Media,Long> {

    List<Media> findByMessageId(Long messageId);

    @Query("""
    SELECT me
    FROM Media me
    WHERE me.message.chat.id = :chatId
      AND me.mediaType IN :mediaTypes
""")
    List<Media> findByMessageChatId(
            @Param("chatId") Long chatId,
            @Param("mediaTypes") List<Media.MediaType> mediaTypes
    );





}
