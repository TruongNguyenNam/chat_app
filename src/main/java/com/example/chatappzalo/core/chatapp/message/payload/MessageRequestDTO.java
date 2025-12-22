package com.example.chatappzalo.core.chatapp.message.payload;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class MessageRequestDTO {
    private Long chatId;        // chat mà tin nhắn thuộc về (private/group)
//    private Long senderId;      // id người gửi
    private String content;     // nội dung text
    private List<String> mediaUrls; // để frontend nhận danh sách ảnh/video   // link file ảnh/video/voice (nếu có)
    private String messageType;
    private Long parentMessageId;

}
