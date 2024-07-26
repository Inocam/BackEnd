package com.sparta.backend.chat.controller;

import com.sparta.backend.chat.dto.chatMessage.ChatMessageRequestDto;
import com.sparta.backend.chat.dto.chatMessage.ChatMessageResponseDto;
import com.sparta.backend.chat.service.ChatMessageService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/foot/chat")
public class ChatMessageController {

    private ChatMessageService chatMessageService;

    public ChatMessageController(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    // 채팅 전송
    @PostMapping("/{roomId}/messages")
    public ChatMessageResponseDto createChatMessage(@PathVariable Long roomId,
                                                    @RequestBody ChatMessageRequestDto chatMessageRequestDto) {

        return chatMessageService.createChatMessage(roomId, chatMessageRequestDto);
    }
}
