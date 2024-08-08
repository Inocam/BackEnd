package com.sparta.backend.workspace.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsersInTeamResponseDto {
    private Long teamId;
    private Long userId;
    private String userName;
    private String email;

    public UsersInTeamResponseDto(Long teamId, Long userId, String userName, String email) {
        this.teamId = teamId;
        this.userId = userId;
        this.userName = userName;
        this.email = email;
    }
}
