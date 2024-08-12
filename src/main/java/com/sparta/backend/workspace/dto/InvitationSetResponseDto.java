package com.sparta.backend.workspace.dto;

import com.sparta.backend.workspace.entity.Invitation;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class InvitationSetResponseDto {

    private Long id;
    private Long userId;
    private Long teamId;
    private LocalDateTime invitationReceivedAt;


    public InvitationSetResponseDto(Invitation invitation) {
        this.id = invitation.getId();
        this.userId = invitation.getUser().getId();
        this.teamId = invitation.getTeam().getTeamId();
        this.invitationReceivedAt = invitation.getInvitationReceivedAt();
    }
}