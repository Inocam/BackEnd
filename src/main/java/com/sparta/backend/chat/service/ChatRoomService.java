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
import com.sparta.backend.chat.global.CustomException;
import com.sparta.backend.chat.repository.ChatMessageRepository;
import com.sparta.backend.chat.repository.ChatRoomRepository;
import com.sparta.backend.chat.repository.UserRoomRepository;
import com.sparta.backend.user.model.User;
import com.sparta.backend.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.sparta.backend.chat.global.ErrorCode.*;

@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRoomRepository userRoomRepository;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;

    public ChatRoomService(ChatRoomRepository chatRoomRepository,
                           UserRoomRepository userRoomRepository,
                           UserRepository userRepository,
                           ChatMessageRepository chatMessageRepository) {

        this.chatRoomRepository = chatRoomRepository;
        this.userRoomRepository = userRoomRepository;
        this.userRepository = userRepository;
        this.chatMessageRepository = chatMessageRepository;
    }

    //채팅방 생성
    public ChatRoomResponseDto createRoom(ChatRoomRequestDto chatRoomRequestDto) {

        //  중복된 채팅방 존재 여부
        if (chatRoomRepository.existsByRoomName(chatRoomRequestDto.getRoomName())) {
            throw new CustomException(400, CHATROOM_ALREADY_EXISTS, "이미 존재하는 채팅방 이름입니다.");
        }

        // 사용자 존재 여부
        User user = userRepository.findById(chatRoomRequestDto.getUserId())
                .orElseThrow(() -> new CustomException(404, USER_NOT_FOUND, "사용자가 없습니다."));

        // target 사용자 존재 여부
        User targetUser = userRepository.findById(chatRoomRequestDto.getTargetId())
                .orElseThrow(() -> new CustomException(404, USER_NOT_FOUND, "target 사용자가 없습니다."));



        // ChatRoom 엔티티 생성
        ChatRoom chatRoom = new ChatRoom(chatRoomRequestDto, user);

        // ChatRoom 저장
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        // UserRoom 엔티티 생성
        UserRoom userRoom = new UserRoom(savedChatRoom, user);
        UserRoom targetUserRoom = new UserRoom(savedChatRoom, targetUser);

        // UserRoom 저장
        userRoomRepository.save(userRoom);
        userRoomRepository.save(targetUserRoom);

        String targetUserName = targetUser.getUsername();

        return new ChatRoomResponseDto(userRoom.getId(), user.getId(), targetUser.getId(), chatRoom.getRoomName(), targetUserName);
    }

    // 채팅방 전체 조회
    public Page<RoomListResponseDto> getListRoom(int page, int size) {

        Pageable pageable = PageRequest.of(page-1, size);
        Page<ChatRoom> chatRoomPage = chatRoomRepository.findAllByIsDeletedFalse(pageable);

        // 각 채팅방에 대해 마지막 메시지를 가져와서 RoomListResponseDto 객체를 생성
        List<RoomListResponseDto> roomList = new ArrayList<>();

        for (ChatRoom chatRoom : chatRoomPage) {

            List<ChatMessage> messageList = chatMessageRepository.findAllByChatRoomOrderBySendDateAsc(chatRoom);
            ChatMessage lastMessage = messageList.isEmpty() ? null : messageList.get(messageList.size() - 1);

            LastMessageResponseDto lastMessageDto = new LastMessageResponseDto();
            if (lastMessage != null) {
                lastMessageDto.setUserId(lastMessage.getUser().getId());
                lastMessageDto.setMessage(lastMessage.getMessage());
                lastMessageDto.setSendDate(lastMessage.getSendDate());
            }

            RoomListResponseDto roomListResponseDto = new RoomListResponseDto(chatRoom, lastMessageDto);
            roomList.add(roomListResponseDto);
        }

        // sendDate 기준으로 정렬, null 값은 가장 뒤로 이동
        roomList.sort((room1, room2) -> {
            if (room1.getLastMessage() == null || room1.getLastMessage().getSendDate() == null) {
                return 1;
            }
            if (room2.getLastMessage() == null || room2.getLastMessage().getSendDate() == null) {
                return -1;
            }
            return room1.getLastMessage().getSendDate().compareTo(room2.getLastMessage().getSendDate());
        });

        return new PageImpl<>(roomList, pageable, chatRoomPage.getTotalElements());
    }

    // 사용자가 속한 채팅방 조회
    public Page<RoomListResponseDto> getRoom(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);

        // 사용자가 속한 모든 채팅방을 조회
        Page<UserRoom> userRoomPage = userRoomRepository.findByUserId(userId, pageable);

        List<RoomListResponseDto> roomList = new ArrayList<>();

        for (UserRoom userRoom : userRoomPage) {
            ChatRoom chatRoom = userRoom.getChatRoom();

            // 채팅방이 삭제되지 않은 경우
            if (!chatRoom.isDeleted()) {
                List<ChatMessage> messageList = chatMessageRepository.findTopByChatRoomOrderBySendDateDesc(chatRoom);
                ChatMessage lastMessage = messageList.isEmpty() ? null : messageList.get(0);

                LastMessageResponseDto lastMessageDto = new LastMessageResponseDto();
                if (lastMessage != null) {
                    lastMessageDto.setUserId(lastMessage.getUser().getId());
                    lastMessageDto.setMessage(lastMessage.getMessage());
                    lastMessageDto.setSendDate(lastMessage.getSendDate());
                }

                // 상대방 사용자 이름을 찾기
                UserRoom targetUserRoom = userRoomRepository.findByChatRoomAndUserIdNot(chatRoom, userId);
                String targetUserName = targetUserRoom != null ? targetUserRoom.getUser().getUsername() : "null";

                RoomListResponseDto roomListResponseDto = new RoomListResponseDto(chatRoom, lastMessageDto, targetUserName);
                roomList.add(roomListResponseDto);
            }
        }

        // sendDate 기준으로 정렬, null 값은 가장 뒤로 정렬
        roomList.sort((room1, room2) -> {
            if (room1.getLastMessage() == null || room1.getLastMessage().getSendDate() == null) {
                return 1;
            }
            if (room2.getLastMessage() == null || room2.getLastMessage().getSendDate() == null) {
                return -1;
            }
            return room2.getLastMessage().getSendDate().compareTo(room1.getLastMessage().getSendDate());
        });

        return new PageImpl<>(roomList, pageable, userRoomPage.getTotalElements());
    }

    // 채팅방 삭제
    @Transactional
    public String deleteRoom(Long roomId, Long userId) {

        // 채팅방 존재 여부 확인
        ChatRoom chatRoom = chatRoomRepository.findByRoomIdAndIsDeletedFalse(roomId)
                .orElseThrow(() -> new CustomException(404, CHATROOM_NOT_FOUND, "채팅방이 존재하지 않습니다."));

        // 사용자가 채팅방에 속해 있는지 확인
        boolean userInRoom = false;
        for (UserRoom userRoom : chatRoom.getUserRooms()) {
            if (userRoom.getUser().getId().equals(userId)) {
                userInRoom = true;
                break;
            }
        }

        if (!userInRoom) {
            throw new CustomException(400, USER_NOT_IN_ROOM, "사용자는 이 채팅방에 속해 있지 않아 삭제할 수 없습니다.");
        }

        // softDelete 삭제
        chatRoom.setDeleted(true);

        return "채팅방이 삭제되었습니다.";
    }

    //채팅방 참여
    public UserRoomResponseDto createUserRoom(UserRoomRequestDto userRoomRequestDto) {

        // 채팅방 존재 여부
        ChatRoom chatRoom = chatRoomRepository.findByRoomIdAndIsDeletedFalse(userRoomRequestDto.getRoomId())
                .orElseThrow(() -> new CustomException(404, CHATROOM_NOT_FOUND, "채팅방이 존재하지 않습니다."));

        // 사용자 존재 여부
        User user = userRepository.findById(userRoomRequestDto.getUserId())
                .orElseThrow(() -> new CustomException(400, USER_NOT_IN_ROOM, "해당 사용자는 이 채팅방에 속해 있지 않습니다."));

        // 채팅방에 사람이 있을 경우
        boolean userAlreadyInRoom = userRoomRepository.existsByChatRoomAndUser(chatRoom, user);
        if (userAlreadyInRoom) {
            throw new CustomException(400, USER_ALREADY_IN_ROOM, "해당 사용자가 이미 채팅방에 존재합니다.");
        }

        // UserRoom 엔티티 생성
        UserRoom userRoom = new UserRoom(chatRoom, user);

        // UserRoom 저장
        UserRoom savedUserRoom = userRoomRepository.save(userRoom);

        return new UserRoomResponseDto(savedUserRoom);
    }
}