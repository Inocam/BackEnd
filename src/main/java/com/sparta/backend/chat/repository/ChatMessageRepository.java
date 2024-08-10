package com.sparta.backend.chat.repository;

import com.sparta.backend.chat.entity.ChatMessage;
import com.sparta.backend.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoom(ChatRoom chatRoom);

    List<ChatMessage> findAllByChatRoomOrderBySendDateAsc(ChatRoom chatRoom);
}
