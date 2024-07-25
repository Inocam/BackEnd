package com.sparta.backend.chat.dto;

import com.sparta.backend.chat.entity.User;
import lombok.Getter;

@Getter
public class UserResponseDto {

    private Long userId;
    private String userName;

    public UserResponseDto(User user) {
        this.userId = user.getUserId();
        this.userName = user.getUserName();
    }
}
