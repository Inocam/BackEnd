package com.sparta.backend.chat.dto.chatRoom;

import com.sparta.backend.chat.entity.ChatRoom;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RoomListResponseDto {

    private Long roomId;
    private String targetUserName;
    private String roomName;
    private LastMessageResponseDto lastMessage;
    private LocalDateTime createdDate;

    public RoomListResponseDto(ChatRoom chatRoom, LastMessageResponseDto lastMessage) {
        this.roomId = chatRoom.getRoomId();
        this.roomName = chatRoom.getRoomName();
        this.createdDate = LocalDateTime.now();
        this.lastMessage = lastMessage;
    }

    public RoomListResponseDto(ChatRoom chatRoom, LastMessageResponseDto lastMessage, String targetUserName) {
        this.roomId = chatRoom.getRoomId();
        this.targetUserName = targetUserName;
        this.roomName = chatRoom.getRoomName();
        this.createdDate = LocalDateTime.now();
        this.lastMessage = lastMessage;
    }
}
