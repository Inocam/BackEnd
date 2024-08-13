package com.sparta.backend.workspace.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UsersInTeamResponseDto {
    private Long teamId;
    private Long userId;
    private String userName;
    private String email;
    private LocalDateTime joinedAt;

    public UsersInTeamResponseDto(Long teamId, Long userId, String userName, String email, LocalDateTime joinedAt) {
        this.teamId = teamId;
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.joinedAt = joinedAt;
    }
}
