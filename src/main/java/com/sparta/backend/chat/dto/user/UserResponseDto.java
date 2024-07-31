package com.sparta.backend.chat.dto.user;

import com.sparta.backend.chat.entity.User;
import lombok.Getter;

@Getter
public class UserResponseDto {

    private Long id;
    private String userName;

    public UserResponseDto(User user) {
        this.id = user.getUserId();
        this.userName = user.getUserName();
    }
}

