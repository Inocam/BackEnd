package hello.workspace.dto;

import hello.workspace.entity.TeamUser;
import lombok.Getter;

@Getter
public class ResponseTeamUserDto {

    private Long id;
    private Long userId;
    private Long teamId;
    private Long role;

    public ResponseTeamUserDto(TeamUser teamUser) {
        this.id = teamUser.getId();
        this.userId = teamUser.getUser().getId();
        this.teamId = teamUser.getTeam().getTeam_id();
        this.role = teamUser.getRole();

    }
}
