package com.sparta.backend.workspace.dto;

import com.sparta.backend.workspace.entity.Team;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ResponseTeamInvitationIdDto {

    private Long invitationId; //추가
    private Long teamId;
    private String name;
    private String description;
    private Long creatorId;
    private String creatorName;
    private LocalDateTime creationDate;
    private String imageUrl;

    // 초대장 포함 시 사용할 생성자
    public ResponseTeamInvitationIdDto(Long invitationId, Team team, String creatorName) {
        this.invitationId = invitationId;  // 초대장 ID 설정
        this.teamId = team.getTeamId();
        this.name = team.getName();
        this.description = team.getDescription();
        this.creatorId = team.getCreatorId();
        this.creatorName = creatorName;
        this.imageUrl = team.getImageUrl();
    }
}

