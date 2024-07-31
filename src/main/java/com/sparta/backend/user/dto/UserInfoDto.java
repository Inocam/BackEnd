package com.sparta.backend.user.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfoDto {
    Long userId;
    String username;
    String email;
}