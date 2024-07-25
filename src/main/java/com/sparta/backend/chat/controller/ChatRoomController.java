package com.sparta.backend.chat.controller;

import com.sparta.backend.chat.dto.ChatRoomRequestDto;
import com.sparta.backend.chat.dto.ChatRoomResponseDto;
import com.sparta.backend.chat.service.ChatRoomService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/foot/chat")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    public ChatRoomController(ChatRoomService chatRoomService) {
        this.chatRoomService = chatRoomService;
    }

    // 채팅방 생성
    @PostMapping("/rooms")
    public ChatRoomResponseDto createRoom(@RequestBody ChatRoomRequestDto chatRoomRequestDto) {
        return chatRoomService.createRoom(chatRoomRequestDto);
    }

    // 채팅방 전체 조회
    @GetMapping("/rooms")
    public List<ChatRoomResponseDto> getRoom() {
        return chatRoomService.getRoom();
    }

    // 채팅방 삭제
    @DeleteMapping("/rooms/{roomId}")
    public Long deleteRoom(@PathVariable Long roomId) {
        return chatRoomService.deleteRoom(roomId);
    }
}
