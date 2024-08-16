package com.sparta.backend.chat.repository;

import com.sparta.backend.chat.entity.ChatMessage;
import com.sparta.backend.chat.entity.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findAllByChatRoomOrderBySendDateAsc(ChatRoom chatRoom);

    Page<ChatMessage> findByChatRoomrderBySendDateAsc(ChatRoom chatRoom, Pageable pageable);

    List<ChatMessage> findTopByChatRoomOrderBySendDateDesc(ChatRoom chatRoom);
}
