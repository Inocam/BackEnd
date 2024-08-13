package com.sparta.backend.chat.repository;

import com.sparta.backend.chat.entity.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    boolean existsByRoomName(String roomName);

    Page<ChatRoom> findAllByIsDeletedFalse(Pageable pageable);

    Optional<ChatRoom> findByRoomIdAndIsDeletedFalse(Long roomId);

}

