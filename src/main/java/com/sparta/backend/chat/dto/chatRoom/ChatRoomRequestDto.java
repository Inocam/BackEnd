package com.sparta.backend.chat.dto.chatRoom;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRoomRequestDto {

    private Long userId;
    private String roomName;
}
