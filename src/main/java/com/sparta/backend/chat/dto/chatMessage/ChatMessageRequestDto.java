package com.sparta.backend.chat.dto.chatMessage;

import lombok.Getter;

@Getter
public class ChatMessageRequestDto {

    private Long userId;
    private String message;
}