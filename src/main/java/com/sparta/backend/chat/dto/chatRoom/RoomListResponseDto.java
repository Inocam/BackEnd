package com.sparta.backend.chat.dto.chatRoom;

import com.sparta.backend.chat.entity.ChatRoom;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RoomListResponseDto {

    private Long roomId;
    private String roomName;
    private LastMessageResponseDto lastMessage;
    private LocalDateTime createdDate;

    public RoomListResponseDto(ChatRoom chatRoom, LastMessageResponseDto lastMessage) {
        this.roomId = chatRoom.getRoomId();
        this.roomName = chatRoom.getRoomName();
        this.createdDate = LocalDateTime.now();
        this.lastMessage = lastMessage;
    }

}
