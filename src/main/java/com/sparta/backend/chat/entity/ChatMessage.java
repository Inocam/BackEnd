package com.sparta.backend.chat.entity;

import com.sparta.backend.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @ManyToOne
    @JoinColumn(name = "roomId", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @Column(name="message", nullable = false)
    private String message;

    @Column(name="send_Date", nullable = false)
    private LocalDateTime sendDate;

    public ChatMessage(User user, ChatRoom chatRoom, String message) {
        this.user = user;
        this.chatRoom = chatRoom;
        this.message = message;
        this.sendDate = LocalDateTime.now();
    }
}