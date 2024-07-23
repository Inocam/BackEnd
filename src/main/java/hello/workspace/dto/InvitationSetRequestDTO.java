package hello.workspace.dto;

import lombok.Getter;

@Getter
public class InvitationSetRequestDTO {
    private long invitationId;
    private boolean isAccept; // 수락했는지 -> boolean -> true or false
}
