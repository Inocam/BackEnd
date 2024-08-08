package com.sparta.backend.workspace.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TeamUpdateResponseDto {
    private Long teamId;
    private String name;
    private String description;
    private String imageUrl;

    public TeamUpdateResponseDto(Long teamId, String name, String description, String imageUrl) {
        this.teamId = teamId;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
    }
}
