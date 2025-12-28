package com.example.chatappzalo.repositories;

// Entity for Notifications

import com.example.chatappzalo.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification,Long> {

    List<Notification> findByUserIdOrderByCreatedDateDesc(Long userId);


    @Query("""
    SELECT COUNT(n)
    FROM Notification n
    WHERE n.user.id = :userId
    """)
    long countUnreadByUserId(@Param("userId") Long userId);



}
