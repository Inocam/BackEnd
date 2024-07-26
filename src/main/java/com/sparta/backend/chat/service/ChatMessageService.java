package com.sparta.backend.chat.service;

import com.sparta.backend.chat.dto.chatMessage.ChatMessageRequestDto;
import com.sparta.backend.chat.dto.chatMessage.ChatMessageResponseDto;
import com.sparta.backend.chat.dto.chatMessage.ReadMessageResponseDto;
import com.sparta.backend.chat.entity.ChatMessage;
import com.sparta.backend.chat.entity.ChatRoom;
import com.sparta.backend.chat.entity.User;
import com.sparta.backend.chat.repository.ChatMessageRepository;
import com.sparta.backend.chat.repository.ChatRoomRepository;
import com.sparta.backend.chat.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;

    public ChatMessageService(ChatMessageRepository chatMessageRepository,
                              UserRepository userRepository,
                              ChatRoomRepository chatRoomRepository) {

        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
        this.chatRoomRepository = chatRoomRepository;
    }

    // 채팅 전송
    public ChatMessageResponseDto createChatMessage(Long roomId, ChatMessageRequestDto chatMessageRequestDto) {

        // 사용자 정보 가져오기
        User user = userRepository.findById(chatMessageRequestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        // 초대된 사용자 정보 가져오기
        User sender = userRepository.findById(chatMessageRequestDto.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("보낸 사용자를 찾을 수 없습니다"));

        // 채팅방 중복 여부
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅 방을 찾을 수 없습니다"));

        // 채팅 메시지 생성
        ChatMessage chatMessage = new ChatMessage(user, sender, chatRoom, chatMessageRequestDto.getMessage());


        // 채팅 메시지 저장
        ChatMessage savedChatMessage = chatMessageRepository.save(chatMessage);

        // 저장된 채팅 메시지를 ChatMessageResponseDto로 변환하여 반환
        return new ChatMessageResponseDto(savedChatMessage);
    }

    // 채팅조회
    public List<ReadMessageResponseDto> getChatMessageList(Long roomId) {

        // 채팅방 중복 여부 확인
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅 방을 찾을 수 없습니다"));

        // 채팅 메시지 목록 조회
        List<ChatMessage> chatMessages = chatMessageRepository.findByChatRoom(chatRoom);

        // ReadMessageResponseDto로 변환하여 반환
        List<ReadMessageResponseDto> responseDto = new ArrayList<>();

        for (ChatMessage chatMessage : chatMessages) {
            responseDto.add(new ReadMessageResponseDto(chatMessage));
        }
        return responseDto;
    }
}
