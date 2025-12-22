package com.example.chatappzalo.repositories;

import com.example.chatappzalo.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message,Long> {
     List<Message> findBySenderId(Long senderId);

    @Query("select m from Message m join User u on m.sender.id = u.id " +
            "left join MessageReactions mr on m.id = mr.message.id where m.chat.id =:chatId and m.deleted = false  order by m.sentAt asc "
            )
    List<Message> findByChatId(@Param("chatId") Long chatId);

    @Modifying
    @Query("UPDATE Message m SET m.isRead = :isRead WHERE m.chat.id = :chatId AND m.sender.id != :userId")
    void updateIsReadByChatIdAndSenderIdNot(Long chatId, Long userId, boolean isRead);

}
