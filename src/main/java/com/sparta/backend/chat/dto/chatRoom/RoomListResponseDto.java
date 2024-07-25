package com.sparta.backend.chat.dto.chatRoom;

import com.sparta.backend.chat.entity.ChatRoom;

import java.time.LocalDateTime;

public class RoomListResponseDto {

    private Long roomId;
    private String roomName;
    private LocalDateTime createDate;

    public RoomListResponseDto(ChatRoom chatRoom) {
        this.roomId = chatRoom.getRoomId();
        this.roomName = chatRoom.getRoomName();
        this.createDate = LocalDateTime.now();
    }
}
