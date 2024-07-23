package hello.workspace.dto;

import hello.workspace.entity.Team;
import hello.workspace.entity.TeamUser;
import hello.workspace.entity.User;
import lombok.Getter;

@Getter
public class ResponseTeamUserDto {

    private Long id;
    private User user;
    private Team team;
    private String role;

    public ResponseTeamUserDto(TeamUser teamUser) {
        this.id = teamUser.getId();
        this.user = teamUser.getUser();
        this.team = teamUser.getTeam();
        this.role = teamUser.getRole();

    }
}
