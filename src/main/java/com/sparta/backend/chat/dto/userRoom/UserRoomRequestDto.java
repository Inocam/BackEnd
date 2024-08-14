package com.sparta.backend.chat.dto.userRoom;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRoomRequestDto {

    private Long roomId;
    private Long userId;
}
