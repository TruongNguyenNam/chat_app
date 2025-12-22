package com.example.chatappzalo.core.chatapp.message.payload;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class MessageResponseDTO {
    private Long id;
    private Long chatId;         // id của chat
    private Long senderId;
    private String senderName;   // tên hiển thị người gửi
    private String senderAvatar ; // senderAvatar; // avatar người gửi
    private String content;      // nội dung text
    private List<String> mediaUrl;     // file/ảnh/video/voice
    private String messageType;  // TEXT, IMAGE, VIDEO, VOICE, STICKER, FILE
    private boolean isRead;      // đã đọc hay chưa
    private LocalDateTime sentAt;// thời gian gửi
    private List<String> reactions;    // phản hồi
    private Long parentMessageId;
    private boolean deleted;

}
