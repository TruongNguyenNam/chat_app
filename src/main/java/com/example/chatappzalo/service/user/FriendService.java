package com.example.chatappzalo.service.user;

import com.example.chatappzalo.core.chatapp.user.payload.UserResponseDTO;


public interface FriendService {
    UserResponseDTO findByPhone(String phone);

//    List<LoginInfoDto> getAllUser()
}
