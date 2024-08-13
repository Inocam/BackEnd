package com.sparta.backend.chat.dto.chatMessage;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageRequestDto {

    private Long roomId;
    private Long userId;
    private String message;
}
