package com.example.chatappzalo.repositories;
import com.example.chatappzalo.entity.Auditable;
import com.example.chatappzalo.entity.ChatMember;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ChatMemberRepository extends JpaRepository<ChatMember,Long> {

    //kiểm tra có trong cuộc trò chuyện hay không
    boolean existsByChatIdAndUserId(Long chatId, Long SenderId);

}


