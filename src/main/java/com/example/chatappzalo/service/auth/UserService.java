package com.example.chatappzalo.service.auth;




import com.example.chatappzalo.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {


    User getAccountByUsername(String username);
}
