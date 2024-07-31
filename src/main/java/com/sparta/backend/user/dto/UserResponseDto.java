package com.sparta.backend.user.dto;

import com.sparta.backend.user.model.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDto {
    Long id;
    String username;
    private String email;

    public UserResponseDto(User user){
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
    }
}
