package com.sparta.backend.config;

import com.sparta.backend.chat.handler.ChatHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
//  WebSocketMessageBrokerConfigurer 인터페이스를 구현하여 WebSocket 관련 설정을 제공.
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private  final ChatHandler chatHandler;

    public WebSocketConfig(ChatHandler chatHandler) {
        this.chatHandler = chatHandler;
    }

    @Override
    // WebSocket 메시지 브로커 관련 설정을 구성.
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 메모리 기반 메시지 브로커가 해당 api를 구독하고 있는 클라이언트에게 메시지를 전달.
        // topic : 일반적으로 메시지를 구독하는 클라이언트에게 전달되는 주제(prefix).
        // /foot/chat/rooms: 특정 채팅방에 대한 메시지를 구독하는 클라이언트에게 전달되는 주제(prefix).
        // to subscriber
        registry.enableSimpleBroker("/topic", "foot/chat/rooms");

        // 클라이언트로부터 메시지를 받을 api의 prefix를 설정.
        // /foot/chat/rooms: 클라이언트에서 메시지를 송신할 때 사용하는 주제(prefix).
        // publish
        registry.setApplicationDestinationPrefixes("/foot/chat/rooms");
    }

    @Override
    // WebSocket endpoint를 등록.
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        // socketJs 클라이언트가 WebSocket 핸드셰이크를 하기 위해 연결할 endpoint를 지정.
        registry.addEndpoint("/foot/chat")
                .setAllowedOriginPatterns("*"); // cors 허용을 위해 꼭 설정.
//                .withSockJS(); //웹소켓을 지원하지 않는 브라우저는 sockJS를 사용.
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(chatHandler);
    }
}