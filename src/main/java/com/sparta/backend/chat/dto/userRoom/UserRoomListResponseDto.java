package com.sparta.backend.chat.dto.userRoom;

import com.sparta.backend.chat.entity.UserRoom;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserRoomListResponseDto {

    private Long roomId;
    private String roomName;
    private LocalDateTime createdDate;

    public UserRoomListResponseDto(UserRoom userRoom) {
        this.roomId = userRoom.getChatRoom().getRoomId();
        this.roomName = userRoom.getChatRoom().getRoomName();
        this.createdDate = LocalDateTime.now();
    }
}
