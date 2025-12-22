package com.example.chatappzalo.repositories;


import com.example.chatappzalo.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat,Long> {

    @Query("""
    SELECT c.id FROM Chat c
    WHERE c.chatType = 'PRIVATE'
      AND EXISTS (SELECT 1 FROM c.members m WHERE m.user.id = :userId)
      AND EXISTS (SELECT 1 FROM c.members m WHERE m.user.id = :friendId)
      AND SIZE(c.members) = 2
    """)
    Optional<Long> findPrivateChatIdBetween(@Param("userId") Long userId,
                                            @Param("friendId") Long friendId);

    @Query("""
    select c from Chat c
    join ChatMember m1 on m1.chat = c
    join ChatMember m2 on m2.chat = c
    where c.chatType = 'PRIVATE'
      and m1.user.id = :u1
      and m2.user.id = :u2
""")
    Optional<Chat> findPrivateChat(Long u1, Long u2);


    @Query("""
            select distinct c
            from Chat c
            join c.members m
            where m.user.id = :userId
              and c.deleted = false
              and (c.chatType = 'PRIVATE' or c.chatType = 'GROUP')
            """)
    List<Chat> findAllChatsOfUser(@Param("userId") Long userId);




}
