package com.sparta.backend.chat.dto.chatRoom;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class ChatRoomSendInfoDto {
    String type = "newChat";
    String message;
    LocalDateTime sendDate;
    Long roomId;

    public ChatRoomSendInfoDto(String message, LocalDateTime sendDate, Long roomId) {
        this.message = message;
        this.sendDate = sendDate;
        this.roomId = roomId;
    }
}
