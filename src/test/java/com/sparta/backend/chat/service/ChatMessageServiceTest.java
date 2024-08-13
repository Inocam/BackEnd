package com.sparta.backend.chat.service;

import com.sparta.backend.chat.dto.chatMessage.ChatMessageRequestDto;
import com.sparta.backend.chat.dto.chatMessage.ChatMessageResponseDto;
import com.sparta.backend.chat.dto.chatMessage.ReadMessageResponseDto;
import com.sparta.backend.chat.entity.ChatMessage;
import com.sparta.backend.chat.entity.ChatRoom;
import com.sparta.backend.chat.repository.ChatMessageRepository;
import com.sparta.backend.chat.repository.ChatRoomRepository;
import com.sparta.backend.user.model.User;
import com.sparta.backend.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatMessageServiceTest {

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @InjectMocks
    private ChatMessageService chatMessageService;

    @Test
    @DisplayName("채팅 전송")
    void sendMessage() {

        // Given
        Long roomId = 1L;
        Long userId = 1L;
        String message = "Hi~";

        ChatMessageRequestDto requestDto = new ChatMessageRequestDto();
        requestDto.setUserId(userId);
        requestDto.setMessage(message);

        User user = new User();
        user.setId(userId);

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setRoomId(roomId);

        ChatMessage savedChatMessage = new ChatMessage(user, chatRoom, message);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(chatRoomRepository.findByRoomIdAndIsDeletedFalse(roomId)).thenReturn(Optional.of(chatRoom));
        when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(savedChatMessage);

        // When
        ChatMessageResponseDto responseDto = chatMessageService.sendMessage(roomId, requestDto);

        // Then
        assertEquals(userId, responseDto.getUserId());
        assertEquals(message, responseDto.getMessage());
    }

    @Test
    @DisplayName("채팅 조회")
    void getChatMessageList() {

        // Given
        Long roomId = 1L;

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setRoomId(roomId);

        User user1 = new User();
        user1.setId(1L);

        User user2 = new User();
        user2.setId(2L);

        ChatMessage chatMessage1 = new ChatMessage();
        chatMessage1.setUser(user1);
        chatMessage1.setChatRoom(chatRoom);
        chatMessage1.setMessage("Hello");
        chatMessage1.setSendDate(LocalDateTime.now());

        ChatMessage chatMessage2 = new ChatMessage();
        chatMessage2.setUser(user2);
        chatMessage2.setChatRoom(chatRoom);
        chatMessage2.setMessage("Hi");
        chatMessage2.setSendDate(LocalDateTime.now());

        List<ChatMessage> chatMessages = List.of(chatMessage1, chatMessage2);
        Page<ChatMessage> chatMessagesPage = new PageImpl<>(chatMessages, PageRequest.of(0, 30), chatMessages.size());

        when(chatRoomRepository.findByRoomIdAndIsDeletedFalse(roomId)).thenReturn(Optional.of(chatRoom));
        when(chatMessageRepository.findByChatRoom(chatRoom, PageRequest.of(0, 30))).thenReturn(chatMessagesPage);

        // When
        Page<ReadMessageResponseDto> responsePage = chatMessageService.getChatMessageList(roomId, 1, 30);

        // Then
        assertEquals(chatMessages.size(), responsePage.getTotalElements());
        assertEquals(chatMessages.size(), responsePage.getContent().size());
        assertEquals(chatMessages.get(0).getMessage(), responsePage.getContent().get(0).getMessage());
        assertEquals(chatMessages.get(1).getMessage(), responsePage.getContent().get(1).getMessage());
    }
}
