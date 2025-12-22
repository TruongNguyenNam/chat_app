package com.example.chatappzalo.core.chatapp.user.controller;

import com.example.chatappzalo.core.chatapp.user.payload.UserResponseDTO;
import com.example.chatappzalo.infrastructure.utils.ResponseData;
import com.example.chatappzalo.service.user.FriendService;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/friend")
@Validated
@RequiredArgsConstructor
public class FriendController {

    private final FriendService service;

    @GetMapping("/phone")
    public ResponseData<UserResponseDTO> findByPhone(@RequestParam String phone){
        UserResponseDTO responseDTO = service.findByPhone(phone);
        return new ResponseData<>
                (HttpStatus.SC_OK,
                        "tìm thấy được người dùng",responseDTO);

    }







}
