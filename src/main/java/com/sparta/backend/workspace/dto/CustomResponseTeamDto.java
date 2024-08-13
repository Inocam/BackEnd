package com.sparta.backend.workspace.dto;

import com.sparta.backend.workspace.entity.Team;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CustomResponseTeamDto {
    private Long teamId;
    private String name;
    private String description;
    private Long creatorId;
    private String creatorName;

    public CustomResponseTeamDto(Team team, String creatorName) {
        this.teamId = team.getTeamId();
        this.name = team.getName();
        this.description = team.getDescription();
        this.creatorId = team.getCreatorId();
        this.creatorName = creatorName;
    }
}