package hello.workspace.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsersInTeamResponseDto {
    private Long teamId;
    private Long userId;
    private String userName;

    public UsersInTeamResponseDto(Long teamId, Long userId, String userName) {
        this.teamId = teamId;
        this.userId = userId;
        this.userName = userName;
    }
}
