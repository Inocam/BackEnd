package com.sparta.backend.workspace.dto;

import com.sparta.backend.workspace.entity.Team;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResponseTeamDto {

    private Long team_id;
    private String name;
    private String description;
    private Long creatorId;

    public ResponseTeamDto(Team team) {
        this.team_id = team.getTeam_id();
        this.name = team.getName();
        this.description = team.getDescription();
        this.creatorId = team.getCreatorId();

    }
}
