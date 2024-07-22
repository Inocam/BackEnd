package hello.workspace.dto;

import lombok.Getter;

@Getter
public class RequestInvitationDto {
    private String status;
    private Long teamId;
    private Long userId;

    public RequestInvitationDto(String status, Long teamId, Long userId) {
        this.status = status;
        this.teamId = teamId;
        this.userId = userId;
    }

}
