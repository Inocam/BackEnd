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
import org.springframework.stereotype.Service;
import static com.sparta.backend.chat.global.ErrorCode.*;

import java.time.Instant;
import java.util.*;

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

        // ChatRoom 엔티티 생성
        ChatRoom chatRoom = new ChatRoom(chatRoomRequestDto, user);

        // ChatRoom 저장
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        // UserRoom 엔티티 생성
        UserRoom userRoom = new UserRoom(savedChatRoom, user);

        // UserRoom 저장
        userRoomRepository.save(userRoom);

        return new ChatRoomResponseDto(savedChatRoom);
    }

    // 채팅방 전체 조회
    public List<RoomListResponseDto> getListRoom() {

        List<RoomListResponseDto> roomList = new ArrayList<>();

        List<ChatRoom> chatRooms = chatRoomRepository.findAll();

        // 각 채팅방에 대해 마지막 메시지를 가져와서 RoomListResponseDto 객체를 생성
        for (ChatRoom chatRoom : chatRooms) {

            // 각 채팅방의 모든 메시지를 보낸 시간 기준으로 오름차순으로 가져옴
            List<ChatMessage> messageList = chatMessageRepository.findAllByChatRoomOrderBySendDateAsc(chatRoom);

            // 가장 최신 메시지
            ChatMessage lastMessage = messageList.isEmpty() ? null : messageList.get(messageList.size() - 1);

            // LastMessageResponseDto 생성
            LastMessageResponseDto lastMessageDto = new LastMessageResponseDto();
            if (lastMessage != null) {
                lastMessageDto.setUserId(lastMessage.getUser().getId());
                lastMessageDto.setMessage(lastMessage.getMessage());
                lastMessageDto.setSendDate(lastMessage.getSendDate());
            }

            // RoomListResponseDto 생성
            RoomListResponseDto roomListResponseDto = new RoomListResponseDto(chatRoom, lastMessageDto);
            roomList.add(roomListResponseDto);
        }

        // sendDate 기준으로 정렬, null 값은 가장 뒤로 이동
        roomList.sort((room1, room2) -> {
            // room1의 마지막 메시지가 null이거나 sendDate가 null인 경우
            if (room1.getLastMessage() == null || room1.getLastMessage().getSendDate() == null) {
                return 1; // room1이 room2보다 뒤로 가게 함
            }
            // room2의 마지막 메시지가 null이거나 sendDate가 null인 경우
            if (room2.getLastMessage() == null || room2.getLastMessage().getSendDate() == null) {
                return -1; // room2가 room1보다 뒤로 가게 함
            }
            // sendDate가 null이 아닌 경우 정렬
            return room1.getLastMessage().getSendDate().compareTo(room2.getLastMessage().getSendDate());
        });

        return roomList;
    }

    // 사용자가 속한 채팅방 조회
    public List<UserRoomListResponseDto> getRoom(Long userId) {

        // 사용자가 속한 채팅방 정보 확인
        List<UserRoom> userRooms = userRoomRepository.findAllByUserId(userId);

        // 사용자가 속한 채팅방 존재 여부
        if (userRooms.isEmpty()) {
            throw new CustomException(400, USER_NOT_IN_ROOM, "사용자는 이 채팅방에 속해 있지 않습니다.");
        }

        // 사용자가 속한 채팅방 정보 List 생성
        List<UserRoomListResponseDto> UserRoomListResponseDto = new ArrayList<>();

        // UserRoom 객체를 dto로 변환하여 List에 추가
        for (UserRoom userRoom : userRooms) {
            UserRoomListResponseDto.add(new UserRoomListResponseDto(userRoom));
        }
        return UserRoomListResponseDto;
    }

    // 채팅방 삭제
    @Transactional
    public String deleteRoom(Long roomId, Long userId) {

        // 채팅방 존재 여부 확인
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
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

        // 채팅방과 관련된 UserRoom 엔티티들 삭제
        userRoomRepository.deleteByChatRoom(chatRoom);

        // 채팅방 삭제
        chatRoomRepository.delete(chatRoom);

        return "채팅방이 삭제되었습니다.";
    }

    //채팅방 참여
    public UserRoomResponseDto createUserRoom(UserRoomRequestDto userRoomRequestDto) {

        // 채팅방 존재 여부
        ChatRoom chatRoom = chatRoomRepository.findById(userRoomRequestDto.getRoomId())
                .orElseThrow(() -> new CustomException(404, CHATROOM_NOT_FOUND, "채팅방이 존재하지 않습니다."));

        // 사용자 존재 여부
        User user = userRepository.findById(userRoomRequestDto.getUserId())
                .orElseThrow(() -> new CustomException(400, USER_NOT_IN_ROOM, "해당 사용자는 이 채팅방에 속해 있지 않습니다."));

        // UserRoom 엔티티 생성
        UserRoom userRoom = new UserRoom(chatRoom, user);

        // UserRoom 저장
        UserRoom savedUserRoom = userRoomRepository.save(userRoom);

        return new UserRoomResponseDto(savedUserRoom);
    }
}