package com.sparta.backend.chat.controller;

import com.sparta.backend.chat.dto.chatMessage.ChatMessageRequestDto;
import com.sparta.backend.chat.dto.chatMessage.ChatMessageResponseDto;
import com.sparta.backend.chat.dto.chatMessage.ReadMessageResponseDto;
import com.sparta.backend.chat.dto.chatRoom.ChatRoomRequestDto;
import com.sparta.backend.chat.dto.chatRoom.ChatRoomResponseDto;
import com.sparta.backend.chat.dto.chatRoom.RoomListResponseDto;
import com.sparta.backend.chat.dto.userRoom.UserRoomListResponseDto;
import com.sparta.backend.chat.dto.userRoom.UserRoomRequestDto;
import com.sparta.backend.chat.dto.userRoom.UserRoomResponseDto;
import com.sparta.backend.chat.service.ChatMessageService;
import com.sparta.backend.chat.service.ChatRoomService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/foot/chat/rooms")
public class  ChatController {

    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    public ChatController(ChatRoomService chatRoomService, ChatMessageService chatMessageService) {
        this.chatRoomService = chatRoomService;
        this.chatMessageService = chatMessageService;
    }

    // 채팅방 생성
    @PostMapping()
    public ChatRoomResponseDto createRoom(@RequestBody ChatRoomRequestDto chatRoomRequestDto) {
        return chatRoomService.createRoom(chatRoomRequestDto);
    }

    // 채팅방 전체 조회
    @GetMapping()
    public List<RoomListResponseDto> getListRoom() {
        return chatRoomService.getListRoom();
    }

    // 사용자가 속한 채팅방 조회
    @GetMapping("/{userId}")
    public List<UserRoomListResponseDto> getRoom(@PathVariable Long userId) {
        return chatRoomService.getRoom(userId);
    }

    // 채팅방 삭제
    @DeleteMapping("/{roomId}")
    public String deleteRoom(@PathVariable Long roomId, @RequestParam Long userId) {
        return chatRoomService.deleteRoom(roomId, userId);
    }

    // 채팅방 참여
    @PostMapping("/join")
    public UserRoomResponseDto createUserRoom(@RequestBody UserRoomRequestDto userRoomRequestDto) {
        return chatRoomService.createUserRoom(userRoomRequestDto);
    }

    // 채팅 전송
    // @PostMapping("/{roomId}/messages")
    @MessageMapping("/{roomId}/messages")
    @SendTo("foot/chat/rooms/{roomId}")
    public ChatMessageResponseDto sendMessage(@PathVariable Long roomId,
                                                    @RequestBody ChatMessageRequestDto chatMessageRequestDto) {

        return chatMessageService.sendMessage(roomId, chatMessageRequestDto);
    }

    // 채팅 조회x
    @GetMapping("/{roomId}/messages")
    public List<ReadMessageResponseDto> getChatMessagesList(@PathVariable Long roomId) {
        return chatMessageService.getChatMessageList(roomId);
    }
}
