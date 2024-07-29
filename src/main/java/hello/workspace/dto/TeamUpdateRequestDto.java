package hello.workspace.dto;

import lombok.Getter;

@Getter
public class TeamUpdateRequestDto {
    private String name;
    private String description;

     public TeamUpdateRequestDto(String name, String description) {
         this.name = name;
         this.description = description;
     }
}
