package com.sparta.backend.chat.handler;

import com.sparta.backend.user.security.JwtUtil;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ChatHandler implements ChannelInterceptor {

    private final JwtUtil jwtUtil;

    public ChatHandler(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authToken = accessor.getFirstNativeHeader("Authorization");
            // JWT 검증 로직 추가
            if (!validateToken(authToken)) {
                throw new AccessDeniedException("Invalid token");
            }
        }
        return message;
    }

    private boolean validateToken(String authToken) {

        if (authToken == null || !authToken.startsWith("Bearer ")) {
            return false;
        }

        String token = authToken.substring(7); // "Bearer " 이후의 토큰 추출

        try {
            Claims claims = jwtUtil.getUserInfoFromToken(token);
            log.info("User authenticated: {}", claims.getSubject());
            return true;
        } catch (ExpiredJwtException | MalformedJwtException e) {
            log.error("JWT validation error: {}", e.getMessage());
            return false;
        }
    }
}
