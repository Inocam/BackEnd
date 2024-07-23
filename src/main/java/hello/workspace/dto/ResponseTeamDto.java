package hello.workspace.dto;

import hello.workspace.entity.Team;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResponseTeamDto {

    private Long team_id;
    private String name;
    private String projectName;
    private String description;

    public ResponseTeamDto(Team team) {
        this.team_id = team.getTeam_id();
        this.name = team.getName();
        this.description = team.getDescription();
        this.projectName = team.getProjectName();

    }
}
