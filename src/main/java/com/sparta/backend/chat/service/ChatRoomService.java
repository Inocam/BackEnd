package com.sparta.backend.chat.service;

import com.sparta.backend.chat.dto.chatRoom.ChatRoomRequestDto;
import com.sparta.backend.chat.dto.chatRoom.ChatRoomResponseDto;
import com.sparta.backend.chat.dto.chatRoom.RoomListResponseDto;
import com.sparta.backend.chat.entity.ChatRoom;
import com.sparta.backend.chat.entity.User;
import com.sparta.backend.chat.repository.ChatRoomRepository;
import com.sparta.backend.chat.repository.UserRepository;
import org.springframework.stereotype.Service;

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

        // userId 중복 여부
        User user = userRepository.findById(chatRoomRequestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자가 없습니다."));

        // senderId 중복 여부
        User sender = userRepository.findById(chatRoomRequestDto.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("초대된 사용자가 없습니다."));

        // roomName 중복 여부
        if (chatRoomRepository.existsByRoomName(chatRoomRequestDto.getRoomName())) {
            throw new IllegalArgumentException("이미 존재하는 채팅방 이름입니다.");
        }

        // ChatRoom 엔티티 생성
        ChatRoom chatRoom = new ChatRoom(chatRoomRequestDto, user, sender);

        // ChatRoom 저장
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        return new ChatRoomResponseDto(savedChatRoom);
    }

    // 채팅방 전체 조회
    public List<RoomListResponseDto> getRoom() {

        // 채팅방 정보 List 생성
        List<RoomListResponseDto> chatRoomResponseDto = new ArrayList<>();

        // 생성일 기준 오름차순
        List<ChatRoom> chatRooms = chatRoomRepository.findAllByOrderByCreatedDateAsc();

        // ChatRoom 객체를 dto로 변환하여 List에 추가
        for(ChatRoom chatRoom : chatRooms) {
            chatRoomResponseDto.add(new RoomListResponseDto(chatRoom));
        }

        return chatRoomResponseDto;
    }

    // 채팅방 삭제
    public String deleteRoom(Long roomId) {

        // ChatRoom 중복 확인
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

        // 삭제
        chatRoomRepository.delete(chatRoom);

        return "채팅방이 삭제 되었습니다.";
    }
}