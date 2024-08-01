package com.sparta.backend.chat.dto.userRoom;

import com.sparta.backend.chat.entity.UserRoom;
import lombok.Getter;

@Getter
public class UserRoomResponseDto {

    private Long id;
    private Long roomId;
    private Long userId;

    public UserRoomResponseDto(UserRoom userRoom) {
        this.id = userRoom.getId();
        this.roomId = userRoom.getChatRoom().getRoomId();
        this.userId = userRoom.getUser().getUserId();
    }
}
