package com.sparta.backend.chat.dto.chatRoom;

import com.sparta.backend.chat.entity.ChatRoom;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChatRoomResponseDto {

    private Long roomId;
    private Long userId;
    private Long senderId;
    private String roomName;
    private LocalDateTime createDate;

    public ChatRoomResponseDto(ChatRoom  chatRoom) {
        this.roomId = chatRoom.getRoomId();
        this.userId = chatRoom.getUser().getUserId();
        this.senderId = chatRoom.getSender().getSenderId();
        this.roomName = chatRoom.getRoomName();
        this.createDate = LocalDateTime.now();
    }
}
