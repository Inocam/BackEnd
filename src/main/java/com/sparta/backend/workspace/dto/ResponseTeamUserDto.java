package com.sparta.backend.workspace.dto;

import com.sparta.backend.workspace.entity.User;
import com.sparta.backend.workspace.entity.Team;
import com.sparta.backend.workspace.entity.TeamUser;
import lombok.Getter;

@Getter
public class ResponseTeamUserDto {

    private Long id;
    private User user;
    private Team team;
    private String role;

    public ResponseTeamUserDto(TeamUser teamUser) {
        this.id = teamUser.getId();
        this.user = teamUser.getUser();
        this.team = teamUser.getTeam();
        this.role = teamUser.getRole();

    }
}
