package com.sparta.backend.workspace.dto;

import com.sparta.backend.workspace.entity.Invitation;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvitationResponseDto {

    private Long id;
    private String status;
    private Long userId;
    private Long teamId;


    public InvitationResponseDto(Invitation invitation) {
        this.id = invitation.getId();
        this.status = invitation.getStatus();
        this.userId = invitation.getUser().getId();
        this.teamId = invitation.getTeam().getTeamId();

    }
}
