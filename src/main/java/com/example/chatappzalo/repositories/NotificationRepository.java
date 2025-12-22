package com.example.chatappzalo.repositories;

// Entity for Notifications

import com.example.chatappzalo.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification,Long> {

    List<Notification> findByUserIdOrderByCreatedDateDesc(Long userId);

    Long countByUserIdAndIsReadFalse(Long userId);

}
