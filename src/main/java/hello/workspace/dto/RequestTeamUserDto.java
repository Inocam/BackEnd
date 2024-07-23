package hello.workspace.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestTeamUserDto {

    private Long userId;
    private Long teamId;
    private Long role;
}
