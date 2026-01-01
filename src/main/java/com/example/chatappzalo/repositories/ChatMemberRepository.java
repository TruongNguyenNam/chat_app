package com.example.chatappzalo.repositories;
import com.example.chatappzalo.entity.Auditable;
import com.example.chatappzalo.entity.ChatMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface ChatMemberRepository extends JpaRepository<ChatMember,Long> {

    //kiểm tra có trong cuộc trò chuyện hay không
    boolean existsByChatIdAndUserId(Long chatId, Long SenderId);

    List<ChatMember> findByChatId(Long chatId);

    Optional<ChatMember> findByChatIdAndUserId(Long userId,Long chatId);


    @Query("select coalesce(sum(c.unreadCount),0) from ChatMember c where c.user.id = :userId")
    long sumUnreadMessagesByUserId(@Param("userId") Long userId);

//    List<ChatMember> findByChatId


}


