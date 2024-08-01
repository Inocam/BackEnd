package com.sparta.backend.chat.dto.chatRoom;

import com.sparta.backend.chat.entity.ChatRoom;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChatRoomResponseDto {

    private Long roomId;
    private Long userId;
    private String roomName;
    private LocalDateTime createdDate;

    public ChatRoomResponseDto(ChatRoom chatRoom) {
        this.roomId = chatRoom.getRoomId();
        this.userId = chatRoom.getUser().getUserId();
        this.roomName = chatRoom.getRoomName();
        this.createdDate = LocalDateTime.now();
    }
}

