package com.sparta.backend.chat.dto.user;

import com.sparta.backend.chat.entity.User;
import lombok.Getter;

@Getter
public class UserResponseDto {

    private Long userId;
    private Long senderId;
    private String userName;

    public UserResponseDto(User user) {
        this.userId = user.getUserId();
        this.senderId = user.getSenderId();
        this.userName = user.getUserName();
    }
}
