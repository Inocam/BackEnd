package com.sparta.backend.chat.dto.chatRoom;

import com.sparta.backend.chat.entity.ChatRoom;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChatRoomResponseDto {

    private Long roomId;
    private Long userId;
    private Long targetUserId;
    private String roomName;
    private LocalDateTime createdDate;
    private String targetUserName;

    public ChatRoomResponseDto(ChatRoom chatRoom) {
        this.roomId = chatRoom.getRoomId();
        this.userId = chatRoom.getUser().getId();
        this.roomName = chatRoom.getRoomName();
        this.createdDate = LocalDateTime.now();
    }

    public ChatRoomResponseDto(Long roomId, Long userId, Long targetUserId, String roomName, String targetUserName) {
        this.roomId = roomId;
        this.userId = userId;
        this.targetUserId = targetUserId;
        this.roomName = roomName;
        this.createdDate = LocalDateTime.now();
        this.targetUserName = targetUserName;
    }

    public void setTargetUserName(String targetUserName) {
        this.targetUserName = targetUserName;
    }
}

