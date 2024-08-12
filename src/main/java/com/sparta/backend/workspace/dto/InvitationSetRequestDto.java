package com.sparta.backend.workspace.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class InvitationSetRequestDto {
    private long invitationId;

    @JsonProperty("Accept")
    private boolean Accept; // 수락했는지 -> boolean -> true or false

}