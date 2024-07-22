package hello.workspace.dto;

import hello.workspace.entity.Invitation;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseInvitationDto {

    private Long id;
    private String status;
    private Long userId;
    private Long teamId;

    public ResponseInvitationDto(Invitation invitation) {
        this.id = invitation.getId();
        this.status = invitation.getStatus();
        this.userId = invitation.getUser().getId();
        this.teamId = invitation.getTeam().getTeam_id();

    }
}
