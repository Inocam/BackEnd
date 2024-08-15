package com.sparta.backend.workspace.dto;

import com.sparta.backend.workspace.entity.Invitation;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvitationResponseDto {
    //팀 이름, 팀 설명, 팀장 이름
    Long invitationId;
    Long targetUserId;
    Long teamId;
    String name;
    String description;
    Long creatorId;
    String creatorName;
    String imageUrl;

    public InvitationResponseDto(Invitation invitation) {
        this.invitationId = invitation.getId();
        this.targetUserId = invitation.getUser().getId();
        this.teamId = invitation.getTeam().getTeamId();
    }

    public InvitationResponseDto(Long id, Long targetUserId, Long teamId, String teamName, String description, Long creatorId, String creatorName, String imageUrl) {
        this.invitationId = id;
        this.targetUserId = targetUserId;
        this.teamId = teamId;
        this.name = teamName;
        this.description = description;
        this.creatorId = creatorId;
        this.creatorName = creatorName;
        this.imageUrl = imageUrl;
    }
}
