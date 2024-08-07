package com.sparta.backend.chat.dto.chatRoom;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class LastMessageResponseDto {

    private Long userId;
    private String message;
    private LocalDateTime SendDate;

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSendDate(LocalDateTime sendDate) {
        this.SendDate = sendDate;
    }
}
