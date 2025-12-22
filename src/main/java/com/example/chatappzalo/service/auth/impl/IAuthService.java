package com.example.chatappzalo.service.auth.impl;



import com.example.chatappzalo.core.auth.payload.LoginInfoDto;
import com.example.chatappzalo.core.auth.payload.RegisterForm;
import com.example.chatappzalo.core.auth.payload.UserResponse;
import com.example.chatappzalo.entity.Token;
import com.example.chatappzalo.entity.User;
import com.example.chatappzalo.infrastructure.utils.SecurityUtils;
import com.example.chatappzalo.repositories.UserRepository;
import com.example.chatappzalo.service.auth.AuthService;
import com.example.chatappzalo.service.auth.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class IAuthService implements AuthService {

    private final UserService service;


    private final ModelMapper modelMapper;


    private final UserRepository userRepository;

    private final IJWTTokenServiceImpl ijwtTokenService;

    private final PasswordEncoder passwordEncoder;


    @Override
    public LoginInfoDto login(String username) {

        User entity = service.getAccountByUsername(username);

        LoginInfoDto dto = modelMapper.map(entity, LoginInfoDto.class);

        dto.setToken(ijwtTokenService.generateJWT(entity.getUsername()));

        Token token = ijwtTokenService.generateRefreshToken(entity);
        dto.setRefreshToken(token.getKey());

        return dto;

    }

    @Override
    @Transactional
    public UserResponse register(RegisterForm registerForm) {
        // 1. Kiểm tra username đã tồn tại chưa (nếu muốn)
        if (userRepository.existsByUsername(registerForm.getUsername())) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại");
        }

        // 2. Tạo đối tượng User
        User user = new User();
        user.setUsername(registerForm.getUsername());
        user.setPassword(passwordEncoder.encode(registerForm.getPassword()));
        user.setRole(User.Role.CUSTOMER); // mặc định là CUSTOMER

        // 3. Lưu vào DB
        User savedUser = userRepository.save(user);

        // 4. Chuyển sang DTO để trả về
        return modelMapper.map(savedUser, UserResponse.class);

    }

    @Override
    @Transactional
    public LoginInfoDto
    findByPhone(String phone) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("ID của TÌM KIẾM : " + currentUserId);
        User user = userRepository.findUserByPhoneNoContact(phone,currentUserId).orElseThrow(
                () -> new IllegalArgumentException("không tìm thấy người này")
        );

        return modelMapper.map(user, LoginInfoDto.class);
    }


}
