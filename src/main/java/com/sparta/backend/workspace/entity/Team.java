package com.sparta.backend.workspace.entity;

import com.sparta.backend.workspace.dto.RequestTeamDto;
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
    @Column(name = "team_id")
    private Long teamId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "description", nullable = false, length = 50)
    private String description;

    @Column(name = "creator_id", nullable = false)
    private Long creatorId;

    @Column
    private String imageUrl;

    @Column(name = "is_delete",nullable = false)
    private Boolean isDelete = false;

    public Team(RequestTeamDto requestTeamDto) {
        this.name = requestTeamDto.getName();
        this.description = requestTeamDto.getDescription();
        this.creatorId = requestTeamDto.getCreatorId();
//        this.imageUrl = requestTeamDto.getImageUrl();

    }
    public Team(String name, String description, Long creatorId, String imageUrl) {
        this.name = name;
        this.description = description;
        this.creatorId = creatorId;
        this.imageUrl = imageUrl;
    }
}
