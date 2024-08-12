package com.sparta.backend.workspace.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvitationRequestDto {
    private Long teamId;
    private Long userId;
    private Long requesterId; //요청자 ID 추가

    //기존의 생성자
    public InvitationRequestDto(Long teamId, Long userId, Long requesterId) {
        this.teamId = teamId;
        this.userId = userId;
        this.requesterId = requesterId;
    }

}
