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
    private String message;  //??어떻게 설정해주지(-)

    public InvitationResponseDto(Invitation invitation) {
        this.id = invitation.getId();
        this.status = invitation.getStatus();
        this.userId = invitation.getUser().getId();
        this.teamId = invitation.getTeam().getTeam_id();
        this.message = null; //기본 메시지 설정하거나, 나중에 설정 가능...?
    }
}
