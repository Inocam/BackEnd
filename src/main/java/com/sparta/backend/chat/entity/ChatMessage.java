package com.sparta.backend.chat.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "chatMessage")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "sendTime", nullable = false)
    private LocalDateTime sendDate;

    @ManyToOne
    @JoinColumn(name="userId", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name="senderId", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name="roomId", nullable = false)
    private ChatRoom chatRoom;

    public ChatMessage(User user, User sender, ChatRoom chatRoom, String message) {
        this.user = user;
        this.sender = sender;
        this.chatRoom = chatRoom;
        this.message = message;
        this.sendDate = LocalDateTime.now();
    }
}
