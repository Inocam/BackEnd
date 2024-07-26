package hello.workspace.dto;

import lombok.Getter;

@Getter
public class RequestTeamDto {
    private String name;
    private String description;
    private long creatorId;
}