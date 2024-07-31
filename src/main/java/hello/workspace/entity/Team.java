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

    @Column(name = "description", nullable = false, length = 50)
    private String description;

    @Column(name = "creator_id", nullable = false)
    private Long creatorId;

    @Column
    private String imageUrl;

    public Team(RequestTeamDto requestTeamDto) {
        this.name = requestTeamDto.getName();
        this.description = requestTeamDto.getDescription();
        this.creatorId = requestTeamDto.getCreatorId();

    }
    public Team(String name, String description, Long creatorId, String imageUrl) {
        this.name = name;
        this.description = description;
        this.creatorId = creatorId;
        this.imageUrl = imageUrl;
    }
}
