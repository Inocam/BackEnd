package com.sparta.backend.workspace.dto;

import com.sparta.backend.workspace.entity.Invitation;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class InvitationResponseDto {

    private Long id;
    private Long userId;
    private Long teamId;


    public InvitationResponseDto(Invitation invitation) {
        this.id = invitation.getId();
        this.userId = invitation.getUser().getId();
        this.teamId = invitation.getTeam().getTeamId();

    }
}
