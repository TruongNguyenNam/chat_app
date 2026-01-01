package com.example.chatappzalo.service.auth.impl;

import com.example.chatappzalo.service.auth.OnlineUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class OnlineUserServiceImpl implements OnlineUserService {
    private final Set<Long> onlineUsers = ConcurrentHashMap.newKeySet();

    public boolean remove(Long userId) {
        return onlineUsers.remove(userId);
    }

    public boolean add(Long userId) {
        return onlineUsers.add(userId);
    }

    public boolean isOnline(Long userId) {
        return onlineUsers.contains(userId);
    }

    @Override
    public Set<Long> getAllOnlineUserIds() {
        return new HashSet<>(onlineUsers);
    }
}
