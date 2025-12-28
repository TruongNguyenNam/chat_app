package com.example.chatappzalo.core.chatapp.notification.controller;

import com.example.chatappzalo.core.chatapp.notification.payload.NotificationResponseDTO;
import com.example.chatappzalo.infrastructure.utils.ResponseData;
import com.example.chatappzalo.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notification")
@Validated
@Slf4j
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseData<List<NotificationResponseDTO>> getAllNotification(){
        List<NotificationResponseDTO> notificationResponseDTOS = notificationService.getMyNotifications();

        return ResponseData.<List<NotificationResponseDTO>>builder()
                .status(201)
                .data(notificationResponseDTOS)
                .message("lấy danh sách thông báo của tôi thành công")
                .build();
    }

    @GetMapping("/count")
    public ResponseData<Long> CountNotification() {
        Long count = notificationService.countNotification();

        return ResponseData.<Long>builder()
                .status(200)
                .message("Get notification count successfully")
                .data(count)
                .build();
    }






}
