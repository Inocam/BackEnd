package com.sparta.backend.chat.repository;

import com.sparta.backend.chat.dto.chatRoom.RoomListResponseDto;
import com.sparta.backend.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    boolean existsByRoomName(String roomName);

}

