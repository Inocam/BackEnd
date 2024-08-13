package com.sparta.backend.workspace.dto;

import com.sparta.backend.workspace.entity.Team;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
public class ResponseTeamDto {
    private Long teamId;
    private String name;
    private String description;
    private Long creatorId;
    private String creatorName;
    private LocalDateTime creationDate;
    private String imageUrl;

    // 팀 생성 시 사용할 생성자 (invitationId 없음)
    public ResponseTeamDto(Team team, String creatorName) {
        this.teamId = team.getTeamId();
        this.name = team.getName();
        this.description = team.getDescription();
        this.creatorId = team.getCreatorId();
        this.creatorName = creatorName;
        this.creationDate = LocalDateTime.now();
        this.imageUrl = team.getImageUrl();
    }

}