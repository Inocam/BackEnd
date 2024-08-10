package com.sparta.backend.chat.service;

import com.sparta.backend.chat.dto.chatMessage.ChatMessageRequestDto;
import com.sparta.backend.chat.dto.chatMessage.ChatMessageResponseDto;
import com.sparta.backend.chat.dto.chatMessage.ReadMessageResponseDto;
import com.sparta.backend.chat.entity.ChatMessage;
import com.sparta.backend.chat.entity.ChatRoom;
import com.sparta.backend.chat.global.CustomException;
import com.sparta.backend.chat.repository.ChatMessageRepository;
import com.sparta.backend.chat.repository.ChatRoomRepository;
import com.sparta.backend.user.model.User;
import com.sparta.backend.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import static com.sparta.backend.chat.global.ErrorCode.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    public ChatMessageResponseDto sendMessage(Long roomId, ChatMessageRequestDto chatMessageRequestDto) {

        // 사용자 존재 여부
        User user = userRepository.findById(chatMessageRequestDto.getUserId())
                .orElseThrow(() -> new CustomException(404, USER_NOT_FOUND, "사용자를 찾을 수 없습니다"));

        // 채팅방 중복 여부
        ChatRoom chatRoom = chatRoomRepository.findByRoomIdAndIsDeletedFalse(roomId)
                .orElseThrow(() -> new CustomException(404, CHATROOM_NOT_FOUND, "채팅 방을 찾을 수 없습니다"));

        // ChatMessage 엔티티 생성
        ChatMessage chatMessage = new ChatMessage(user, chatRoom, chatMessageRequestDto.getMessage());

        // ChatMessage 저장
        ChatMessage savedChatMessage = chatMessageRepository.save(chatMessage);

        // ChatMessage 객체를 dto로 변환
        return new ChatMessageResponseDto(savedChatMessage);
    }

    public Page<ReadMessageResponseDto> getChatMessageList(Long roomId, int page, int size) {

        // 채팅방 존재 여부
        ChatRoom chatRoom = chatRoomRepository.findByRoomIdAndIsDeletedFalse(roomId)
                .orElseThrow(() -> new CustomException(404, CHATROOM_NOT_FOUND, "채팅 방을 찾을 수 없습니다"));

        // 페이지네이션 설정
        Pageable pageable = PageRequest.of(page-1, size);

        // 페이지네이션된 ChatMessage 목록 조회
        Page<ChatMessage> chatMessagesPage = chatMessageRepository.findByChatRoom(chatRoom, pageable);

        // ChatMessage 객체를 ReadMessageResponseDto로 변환
        List<ReadMessageResponseDto> readMessageResponseDto = new ArrayList<>();
        for (ChatMessage chatMessage : chatMessagesPage.getContent()) {
            readMessageResponseDto.add(new ReadMessageResponseDto(chatMessage));
        }
        // Page<ReadMessageResponseDto> 객체 생성하여 반환
        return new PageImpl<>(readMessageResponseDto, pageable, chatMessagesPage.getTotalElements());
    }
}
