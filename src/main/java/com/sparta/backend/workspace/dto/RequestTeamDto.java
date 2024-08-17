package com.sparta.backend.workspace.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class RequestTeamDto {
    private String name;
    private String description;
    private long creatorId;

}