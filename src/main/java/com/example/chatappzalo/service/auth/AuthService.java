package com.example.chatappzalo.service.auth;


import com.example.chatappzalo.core.auth.payload.LoginInfoDto;
import com.example.chatappzalo.core.auth.payload.RegisterForm;
import com.example.chatappzalo.core.auth.payload.UserOnlineDTO;
import com.example.chatappzalo.core.auth.payload.UserResponse;
import org.springframework.scheduling.annotation.Async;

import java.time.LocalDateTime;
import java.util.List;

public interface AuthService {
    LoginInfoDto login(String username);


     void logout(Long userId);

    UserResponse register(RegisterForm registerForm);

    LoginInfoDto findByPhone(String phone);

     void markOnline(Long userId);


     void markOffline(Long userId);

    List<UserOnlineDTO> findByIsOnlineTrue();


}
