package com.sparta.backend.chat.controller;

import com.sparta.backend.chat.dto.chatMessage.ChatMessageRequestDto;
import com.sparta.backend.chat.dto.chatMessage.ChatMessageResponseDto;
import com.sparta.backend.chat.dto.chatMessage.ReadMessageResponseDto;
import com.sparta.backend.chat.dto.chatRoom.ChatRoomRequestDto;
import com.sparta.backend.chat.dto.chatRoom.ChatRoomResponseDto;
import com.sparta.backend.chat.dto.chatRoom.ChatRoomSendInfoDto;
import com.sparta.backend.chat.dto.chatRoom.RoomListResponseDto;
import com.sparta.backend.chat.dto.userRoom.UserRoomListResponseDto;
import com.sparta.backend.chat.dto.userRoom.UserRoomRequestDto;
import com.sparta.backend.chat.dto.userRoom.UserRoomResponseDto;
import com.sparta.backend.chat.entity.ChatRoom;
import com.sparta.backend.chat.entity.UserRoom;
import com.sparta.backend.chat.repository.ChatRoomRepository;
import com.sparta.backend.chat.repository.UserRoomRepository;
import com.sparta.backend.chat.service.ChatMessageService;
import com.sparta.backend.chat.service.ChatRoomService;
import com.sparta.backend.user.model.User;
import com.sparta.backend.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/foot/chat/rooms")
@AllArgsConstructor
public class  ChatController {

    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final UserRoomRepository userRoomRepository;
    private final ChatRoomRepository chatRoomRepository;

    // 채팅방 생성
    @PostMapping()
    public void createRoom(@RequestBody ChatRoomRequestDto chatRoomRequestDto) {
        ChatRoomResponseDto chatRoomResponseDto = chatRoomService.createRoom(chatRoomRequestDto);
        messagingTemplate.convertAndSend("/topic/room/" + chatRoomRequestDto.getUserId(),chatRoomResponseDto);

        User targetUser = userRepository.findById(chatRoomRequestDto.getTargetId()).orElse(null);
        chatRoomResponseDto.setTargetUserName(targetUser.getUsername());
        messagingTemplate.convertAndSend("/topic/room/" + chatRoomRequestDto.getTargetId(),chatRoomResponseDto);
    }

    // 채팅방 전체 조회
    @GetMapping()
    public Page<RoomListResponseDto> getListRoom(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return chatRoomService.getListRoom(page, size);
    }

    // 사용자가 속한 채팅방 조회
    @GetMapping("/{userId}")
    public Page<RoomListResponseDto> getRoom(
            @PathVariable Long userId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return chatRoomService.getRoom(userId, page, size);
    }

    // List - 사용자가 속한 채팅방 조회
    @GetMapping("/get/{userId}")
    public List<RoomListResponseDto> getRooms(@PathVariable Long userId) {
        return chatRoomService.getRooms(userId);
    }

    // 채팅방 삭제
    @DeleteMapping("/{roomId}")
    public String deleteRoom(@PathVariable Long roomId, @RequestParam Long userId) {
        return chatRoomService.deleteRoom(roomId, userId);
    }

    // 채팅방 참여
    @PostMapping("/join")
    public UserRoomResponseDto createUserRoom(@RequestBody UserRoomRequestDto userRoomRequestDto) {
//        ChatRoom chatRoom = chatRoomRepository.findById(userRoomRequestDto.getRoomId()).orElse(null);
//        List<UserRoom> foundUserRoom = userRoomRepository.findALlByChatRoom(chatRoom).orElse(null);
//        for(UserRoom userRoom : foundUserRoom) {
//            messagingTemplate.convertAndSend("/topic/room/" + userRoom.getUser().getId(),chatRoomService.createUserRoom(userRoomRequestDto));
//        }

        return chatRoomService.createUserRoom(userRoomRequestDto);
    }

    // WebSocket을 통한 채팅 메시지 전송
    @MessageMapping("/sendMessage")
    public ChatMessageResponseDto sendMessage(@Payload ChatMessageRequestDto chatMessageRequestDto) {
        Long roomId = chatMessageRequestDto.getRoomId();
        ChatMessageResponseDto responseDto = chatMessageService.sendMessage(roomId, chatMessageRequestDto);
        messagingTemplate.convertAndSend("/topic/chat/" + roomId, responseDto);

        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElse(null);
        List<UserRoom> userRooms = userRoomRepository.findAllByChatRoom(chatRoom).orElse(null);
        for(UserRoom userRoom : userRooms) {
            messagingTemplate.convertAndSend("/topic/room/" + userRoom.getUser().getId(), new ChatRoomSendInfoDto(chatMessageRequestDto.getMessage(), LocalDateTime.now(), roomId));
        }

        return responseDto;
    }

    // 채팅 조회
    @GetMapping("/{roomId}/messages")
    public Page<ReadMessageResponseDto> getChatMessagesList(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "30") int size) {

        return chatMessageService.getChatMessageList(roomId, page, size);
    }
}