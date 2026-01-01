package com.example.chatappzalo.service.auth;

import java.util.Set;

public interface OnlineUserService {
     boolean remove(Long userId);

     boolean add(Long userId);

     boolean isOnline(Long userId);

     Set<Long> getAllOnlineUserIds();

}
