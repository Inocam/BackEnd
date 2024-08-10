package com.sparta.backend.workspace.dto;

import lombok.Getter;

@Getter
public class TeamUpdateRequestDto {
    private String name;
    private String description;
    private String imageUrl;

     public TeamUpdateRequestDto(String name, String description, String imageUrl) {
         this.name = name;
         this.description = description;
         this.imageUrl = imageUrl;

     }
}
