package com.sparta.backend.chat.dto.chatRoom;

import lombok.Getter;

@Getter
public class ChatRoomRequestDto {

    private Long userId;
    private Long senderId;
    private String roomName;
}
