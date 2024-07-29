package hello.workspace.dto;

import lombok.Getter;

@Getter
public class TeamUpdateResponseDto {
    private Long teamId;
    private String name;
    private String description;

    public TeamUpdateResponseDto(Long teamId, String name, String description) {
        this.teamId = teamId;
        this.name = name;
        this.description = description;
    }
}
