package com.sparta.backend.workspace.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class TeamUpdateRequestDto implements Serializable {
    private String name;
    private String description;
    private String imageUrl;


     public TeamUpdateRequestDto(String name, String description, String imageUrl) {
         this.name = name;
         this.description = description;
         this.imageUrl = imageUrl;

     }
}
