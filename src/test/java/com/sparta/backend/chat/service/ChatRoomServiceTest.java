package com.sparta.backend.chat.service;

import com.sparta.backend.chat.dto.chatRoom.ChatRoomRequestDto;
import com.sparta.backend.chat.dto.chatRoom.ChatRoomResponseDto;
import com.sparta.backend.chat.dto.chatRoom.LastMessageResponseDto;
import com.sparta.backend.chat.dto.chatRoom.RoomListResponseDto;
import com.sparta.backend.chat.dto.userRoom.UserRoomListResponseDto;
import com.sparta.backend.chat.dto.userRoom.UserRoomRequestDto;
import com.sparta.backend.chat.dto.userRoom.UserRoomResponseDto;
import com.sparta.backend.chat.entity.ChatMessage;
import com.sparta.backend.chat.entity.ChatRoom;
import com.sparta.backend.chat.entity.UserRoom;
import com.sparta.backend.chat.repository.ChatMessageRepository;
import com.sparta.backend.chat.repository.ChatRoomRepository;
import com.sparta.backend.chat.repository.UserRoomRepository;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private UserRoomRepository userRoomRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @InjectMocks
    private ChatRoomService chatRoomService;

    @Test
    @DisplayName("채팅방 생성")
    void createRoom() {

        // Given
        Long userId = 1L;
        String roomName = "Test Room";
        ChatRoomRequestDto requestDto = new ChatRoomRequestDto();
        requestDto.setUserId(userId);
        requestDto.setRoomName(roomName);

        User user = new User();
        user.setId(userId);

        ChatRoom chatRoom = new ChatRoom(requestDto, user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(chatRoomRepository.existsByRoomName(roomName)).thenReturn(false);
        when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(chatRoom);
        when(userRoomRepository.save(any(UserRoom.class))).thenReturn(new UserRoom(chatRoom, user));

        // When
        ChatRoomResponseDto responseDto = chatRoomService.createRoom(requestDto);

        // Then
        assertEquals(roomName, responseDto.getRoomName());
        assertEquals(userId, responseDto.getUserId());
    }

    @Test
    @DisplayName("채팅방 목록 조회")
    void getListRoom() {

        // Given
        User user1 = new User();
        user1.setId(1L);

        User user2 = new User();
        user2.setId(2L);

        ChatRoom chatRoom1 = new ChatRoom();
        chatRoom1.setRoomId(1L);
        chatRoom1.setRoomName("Test Room 1");
        chatRoom1.setCreatedDate(LocalDateTime.now());

        ChatRoom chatRoom2 = new ChatRoom();
        chatRoom2.setRoomId(2L);
        chatRoom2.setRoomName("Test Room 2");
        chatRoom2.setCreatedDate(LocalDateTime.now());

        ChatMessage message1 = new ChatMessage();
        message1.setSendDate(LocalDateTime.now().minusMinutes(10));
        message1.setMessage("Hello from Room 1");
        message1.setUser(user1);  // user 설정

        ChatMessage message2 = new ChatMessage();
        message2.setSendDate(LocalDateTime.now().minusMinutes(5));
        message2.setMessage("Hello from Room 2");
        message2.setUser(user2);  // user 설정

        List<ChatMessage> messagesForRoom1 = Arrays.asList(message1);
        List<ChatMessage> messagesForRoom2 = Arrays.asList(message2);

        when(chatRoomRepository.findAllByIsDeletedFalse(PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(Arrays.asList(chatRoom1, chatRoom2)));

        when(chatMessageRepository.findAllByChatRoomOrderBySendDateAsc(chatRoom1))
                .thenReturn(messagesForRoom1);
        when(chatMessageRepository.findAllByChatRoomOrderBySendDateAsc(chatRoom2))
                .thenReturn(messagesForRoom2);

        // When
        Page<RoomListResponseDto> responseDtoPage = chatRoomService.getListRoom(1, 10);

        // Then
        List<RoomListResponseDto> responseDtos = responseDtoPage.getContent();

        assertEquals(2, responseDtos.size());

        RoomListResponseDto dto1 = responseDtos.get(0);
        RoomListResponseDto dto2 = responseDtos.get(1);

        assertEquals(1L, dto1.getRoomId());
        assertEquals("Test Room 1", dto1.getRoomName());
        assertEquals(2L, dto2.getRoomId());
        assertEquals("Test Room 2", dto2.getRoomName());

        LastMessageResponseDto lastMessageDto1 = dto1.getLastMessage();
        LastMessageResponseDto lastMessageDto2 = dto2.getLastMessage();

        assertEquals("Hello from Room 1", lastMessageDto1.getMessage());
        assertEquals("Hello from Room 2", lastMessageDto2.getMessage());
    }

    @Test
    @DisplayName("사용자가 속한 채팅방 조회")
    void getRoom() {

        // Given
        Long userId = 1L;

        ChatRoom chatRoom1 = new ChatRoom();
        chatRoom1.setRoomId(1L);

        ChatRoom chatRoom2 = new ChatRoom();
        chatRoom2.setRoomId(2L);

        UserRoom userRoom1 = new UserRoom(chatRoom1, new User());
        userRoom1.getUser().setId(userId);

        UserRoom userRoom2 = new UserRoom(chatRoom2, new User());
        userRoom2.getUser().setId(userId);

        List<UserRoom> userRooms = Arrays.asList(userRoom1, userRoom2);

        when(userRoomRepository.findAllByUserId(userId)).thenReturn(userRooms);

        // When
        List<UserRoomListResponseDto> responseDtoList = chatRoomService.getRoom(userId);

        // Then
        assertEquals(2, responseDtoList.size());

        UserRoomListResponseDto dto1 = responseDtoList.get(0);
        UserRoomListResponseDto dto2 = responseDtoList.get(1);

        assertEquals(1L, dto1.getRoomId());
        assertEquals(2L, dto2.getRoomId());
    }

    @Test
    @DisplayName("채팅방 삭제")
    void deleteRoom() {

        // Given
        Long roomId = 1L;
        Long userId = 1L;

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setRoomId(roomId);
        chatRoom.setDeleted(false);

        User user = new User();
        user.setId(userId);

        UserRoom userRoom = new UserRoom(chatRoom, user);
        chatRoom.getUserRooms().add(userRoom);

        when(chatRoomRepository.findByRoomIdAndIsDeletedFalse(roomId)).thenReturn(Optional.of(chatRoom));

        // When
        String result = chatRoomService.deleteRoom(roomId, userId);

        // Then
        assertEquals("채팅방이 삭제되었습니다.", result);
        assertTrue(chatRoom.isDeleted());
    }

    @Test
    @DisplayName("채팅방 참여")
    void createUserRoom() {

        // Given
        Long roomId = 1L;
        Long userId = 1L;

        UserRoomRequestDto requestDto = new UserRoomRequestDto();
        requestDto.setRoomId(roomId);
        requestDto.setUserId(userId);

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setRoomId(roomId);

        User user = new User();
        user.setId(userId);

        UserRoom savedUserRoom = new UserRoom(chatRoom, user);

        when(chatRoomRepository.findByRoomIdAndIsDeletedFalse(roomId)).thenReturn(Optional.of(chatRoom));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRoomRepository.save(any(UserRoom.class))).thenReturn(savedUserRoom);

        // When
        UserRoomResponseDto responseDto = chatRoomService.createUserRoom(requestDto);

        // Then
        assertEquals(roomId, responseDto.getRoomId());
        assertEquals(userId, responseDto.getUserId());
    }
}