package com.sparta.backend.chat.controller;

import com.sparta.backend.chat.dto.chatMessage.ChatMessageRequestDto;
import com.sparta.backend.chat.dto.chatMessage.ChatMessageResponseDto;
import com.sparta.backend.chat.dto.chatMessage.ReadMessageResponseDto;
import com.sparta.backend.chat.service.ChatMessageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/foot/rooms")
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    public ChatMessageController(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    // 채팅 전송
    @PostMapping("/{roomId}/messages")
    public ChatMessageResponseDto createChatMessage(@PathVariable Long roomId,
                                                    @RequestBody ChatMessageRequestDto chatMessageRequestDto) {

        return chatMessageService.createChatMessage(roomId, chatMessageRequestDto);
    }

    // 채팅 조회
    @GetMapping("/{roomId}/messages")
    public List<ReadMessageResponseDto> getChatMessagesList(@PathVariable Long roomId) {
        return chatMessageService.getChatMessageList(roomId);
    }
}

