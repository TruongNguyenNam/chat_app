package com.example.chatappzalo.service.auth;


import com.example.chatappzalo.core.auth.payload.LoginInfoDto;
import com.example.chatappzalo.core.auth.payload.RegisterForm;
import com.example.chatappzalo.core.auth.payload.UserResponse;

public interface AuthService {
    LoginInfoDto login(String username);

    UserResponse register(RegisterForm registerForm);

    LoginInfoDto findByPhone(String phone);



}
