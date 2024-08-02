package com.sparta.backend.chat.repository;

import com.sparta.backend.chat.entity.ChatRoom;
import com.sparta.backend.chat.entity.UserRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRoomRepository extends JpaRepository<UserRoom, Long> {
    void deleteByChatRoom(ChatRoom chatRoom);

    List<UserRoom> findAllByUserId(Long userId);
}
