package com.sparta.backend.workspace.dto;

import com.sparta.backend.workspace.entity.Invitation;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class InvitationResponseDto {
    //팀 이름, 팀 설명, 팀장 이름
    Long id;
    Long userId;
    Long teamId;
    String teamName;
    String description;
    String leaderName;

    public InvitationResponseDto(Invitation invitation) {
        this.id = invitation.getId();
        this.userId = invitation.getUser().getId();
        this.teamId = invitation.getTeam().getTeamId();
    }

    public InvitationResponseDto(Long id, Long userId, Long teamId, String teamName, String description, String leaderName) {
        this.id = id;
        this.userId = userId;
        this.teamId = teamId;
        this.teamName = teamName;
        this.description = description;
        this.leaderName = leaderName;
    }
}
