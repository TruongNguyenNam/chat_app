package com.example.chatappzalo.core.auth.controller;


import com.example.chatappzalo.core.auth.payload.*;
import com.example.chatappzalo.infrastructure.utils.ResponseData;
import com.example.chatappzalo.infrastructure.utils.SecurityUtils;
import com.example.chatappzalo.infrastructure.validation.RefreshTokenValid;
import com.example.chatappzalo.service.auth.impl.IAuthService;
import com.example.chatappzalo.service.auth.impl.IJWTTokenServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "api/v1/auth")
@Validated
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;


    private final IJWTTokenServiceImpl ijwtTokenService;


    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseData<LoginInfoDto> login(@RequestBody @Valid loginForm loginForm) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginForm.getUsername(),
                        loginForm.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        LoginInfoDto loginInfoDto = authService.login(loginForm.getUsername());

        return new ResponseData<>(200, "Đăng nhập thành công", loginInfoDto);
    }


    @PostMapping("/logout")
    public ResponseData<Void> logout() {

        Long userId = SecurityUtils.getCurrentUserId();

        log.info("userID  {}", userId);
        if (userId == null) {
            throw new RuntimeException("User chưa đăng nhập");
        }

        log.info("Logout userId = {}", userId);

        authService.logout(userId);

        return new ResponseData<>(200, "Đăng xuất thành công", null);
    }




    @GetMapping("/refreshToken")
    public ResponseData<TokenDTO> refreshToken(@RefreshTokenValid String refreshToken) {
        try {
            TokenDTO newToken = ijwtTokenService.getNewToken(refreshToken);
            return new ResponseData<>(200, "Token refreshed", newToken);
        } catch (IllegalArgumentException e) {
            return new ResponseData<>(403, "Invalid refresh token");
        }
    }

    @PostMapping("/register")
    public ResponseData<UserResponse> register(@RequestBody @Valid RegisterForm registerForm) {
        UserResponse userResponse = authService.register(registerForm);
        return new ResponseData<>(201, "Đăng Ký Thành công", userResponse);
    }



//    @GetMapping("/phone")
//    public ResponseData<LoginInfoDto> findByPhone(@RequestParam String phone){
//        LoginInfoDto loginInfoDto = authService.findByPhone(phone);
//        return new ResponseData<>(200, "tìm thấy người dùng", loginInfoDto);
//
//    } hàm này sẽ bỏ











}
