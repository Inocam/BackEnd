package hello.workspace.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvitationRequestDto {
    private String status;
    private Long teamId;
    private Long userId;
    private Long requesterId; //요청자 ID 추가

    //기존의 생성자
    public InvitationRequestDto(String status, Long teamId, Long userId, Long requesterId) {
        this.status = status;
        this.teamId = teamId;
        this.userId = userId;
        this.requesterId = requesterId;
    }

}
