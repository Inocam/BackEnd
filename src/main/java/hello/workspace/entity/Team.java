package hello.workspace.entity;

import hello.workspace.dto.RequestTeamDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "team")
@Getter
@Setter
@NoArgsConstructor
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long team_id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "projectName", nullable = false, length = 50)
    private String projectName;

    @Column(name = "description", nullable = false, length = 50)
    private String description;

    public Team(RequestTeamDto requestTeamDto) {
        this.name = requestTeamDto.getName();
        this.projectName = requestTeamDto.getProjectName();
        this.description = requestTeamDto.getDescription();
    }
}
