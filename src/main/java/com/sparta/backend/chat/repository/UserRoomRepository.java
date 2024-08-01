package com.sparta.backend.chat.repository;

import com.sparta.backend.chat.entity.ChatRoom;
import com.sparta.backend.chat.entity.UserRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoomRepository extends JpaRepository<UserRoom, Long> {
    void deleteByChatRoom(ChatRoom chatRoom);
}
