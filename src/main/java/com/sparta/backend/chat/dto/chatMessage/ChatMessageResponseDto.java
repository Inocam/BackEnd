package com.sparta.backend.chat.dto.chatMessage;

import com.sparta.backend.chat.entity.ChatMessage;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChatMessageResponseDto {

    private Long messageId;
    private Long userId;
    private Long senderId;
    private String message;
    private LocalDateTime sendDate;

    public ChatMessageResponseDto(ChatMessage chatMessage) {
        this.messageId = chatMessage.getMessageId();
        this.userId = chatMessage.getUser().getUserId();
        this.senderId = chatMessage.getSender().getSenderId();
        this.message = chatMessage.getMessage();
        this.sendDate = LocalDateTime.now();
    }
}
