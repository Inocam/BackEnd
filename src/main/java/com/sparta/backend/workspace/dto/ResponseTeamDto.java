package com.sparta.backend.workspace.dto;

import com.sparta.backend.workspace.entity.Team;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ResponseTeamDto {

    private Long team_id;
    private String name;
    private String description;
    private Long creatorId;
    private String creatorName;

    public ResponseTeamDto(Team team, String creatorName) {
        this.team_id = team.getTeam_id();
        this.name = team.getName();
        this.description = team.getDescription();
        this.creatorId = team.getCreatorId();
        this.creatorName = creatorName;
    }

    public ResponseTeamDto(Team team) {
    }
}
