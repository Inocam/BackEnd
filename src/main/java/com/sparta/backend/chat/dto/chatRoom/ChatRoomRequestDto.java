package com.sparta.backend.chat.dto.chatRoom;

import lombok.Getter;
import lombok.Setter;

@Getter
public class ChatRoomRequestDto {

    private Long userId;
    private Long targetId;
    private String roomName;
}
