package hello.workspace.dto;

import lombok.Getter;

@Getter
public class RequestTeamDto {

    private Long team_id;
    private String name;
    private String projectName;
    private String description;

    public RequestTeamDto(Long team_id, String name, String projectName, String description) {
        this.team_id = team_id;
        this.name = name;
        this.projectName = projectName;
        this.description = description;
    }
}
