package com.sparta.backend.chat.repository;

import com.sparta.backend.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    boolean existsByRoomName(String roomName);

    List<ChatRoom> findAllByIsDeletedFalse();

    Optional<ChatRoom> findByRoomIdAndIsDeletedFalse(Long roomId);
}

