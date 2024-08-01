package com.sparta.backend.chat.service;

import com.sparta.backend.chat.dto.chatRoom.ChatRoomRequestDto;
import com.sparta.backend.chat.dto.chatRoom.ChatRoomResponseDto;
import com.sparta.backend.chat.dto.chatRoom.RoomListResponseDto;
import com.sparta.backend.chat.dto.userRoom.UserRoomRequestDto;
import com.sparta.backend.chat.dto.userRoom.UserRoomResponseDto;
import com.sparta.backend.chat.entity.ChatRoom;
import com.sparta.backend.chat.entity.UserRoom;
import com.sparta.backend.chat.global.CustomException;
import com.sparta.backend.chat.repository.ChatRoomRepository;
import com.sparta.backend.chat.repository.UserRoomRepository;
import com.sparta.backend.user.model.User;
import com.sparta.backend.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import static com.sparta.backend.chat.global.ErrorCode.*;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRoomRepository userRoomRepository;
    private final UserRepository userRepository;

    public ChatRoomService(ChatRoomRepository chatRoomRepository,
                           UserRoomRepository userRoomRepository,
                           UserRepository userRepository) {

        this.chatRoomRepository = chatRoomRepository;
        this.userRoomRepository = userRoomRepository;
        this.userRepository = userRepository;
    }

    //채팅방 생성
    public ChatRoomResponseDto createRoom(ChatRoomRequestDto chatRoomRequestDto) {

        //  같은 이름의 채팅방 존재 여부
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
            throw new CustomException(400, USER_NOT_IN_ROOM, "해당 사용자는 이 채팅방에 속해 있지 않아 삭제할 수 없습니다.");
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