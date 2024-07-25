package com.sparta.backend.chat.service;

import com.sparta.backend.chat.dto.ChatRoomRequestDto;
import com.sparta.backend.chat.dto.ChatRoomResponseDto;
import com.sparta.backend.chat.entity.ChatRoom;
import com.sparta.backend.chat.entity.User;
import com.sparta.backend.chat.repository.ChatRoomRepository;
import com.sparta.backend.chat.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    public ChatRoomService(ChatRoomRepository chatRoomRepository, UserRepository userRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.userRepository = userRepository;

    }

    //채팅방 생성
    public ChatRoomResponseDto createRoom(ChatRoomRequestDto chatRoomRequestDto) {

        // 사용자 정보 가져오기
        User user = userRepository.findById(chatRoomRequestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자가 없습니다."));

        // 채팅방 중복 여부
        if (chatRoomRepository.existsByRoomName(chatRoomRequestDto.getRoomName())) {
            throw new IllegalArgumentException("이미 존재하는 채팅방 이름입니다.");
        }

        // ChatRoom 엔티티 생성
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setRoomName(chatRoomRequestDto.getRoomName());
        chatRoom.setCreatedDate(LocalDateTime.now());
        chatRoom.setUser(user);

        // ChatRoom 저장 및 응답 DTO 생성
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        return new ChatRoomResponseDto(savedChatRoom);
    }

    // 채팅방 전체 조회
    public List<ChatRoomResponseDto> getRoom() {

        List<ChatRoomResponseDto> chatRoomResponseDto = new ArrayList<>();
        List<ChatRoom> chatRooms = chatRoomRepository.findAllByOrderByCreatedDateAsc();

        for(ChatRoom chatRoom : chatRooms) {
            chatRoomResponseDto.add(new ChatRoomResponseDto(chatRoom));
        }

        return chatRoomResponseDto;
    }

    // 채팅방 삭제
    public Long deleteRoom(Long roomId) {

        // 채팅방 존재 여부 확인
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

        chatRoomRepository.delete(chatRoom);

        return roomId;
    }
}