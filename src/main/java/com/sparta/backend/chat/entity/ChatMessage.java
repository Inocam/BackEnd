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
    @JoinColumn(name="userId")
    private User user;

    @ManyToOne
    @JoinColumn(name="senderId")
    private User sender;

    @ManyToOne
    @JoinColumn(name="chatRoomId")
    private ChatRoom chatRoom;

    public void setUser(User user) {
        this.user = user;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSendDate(LocalDateTime now) {
        this.sendDate = now;
    }
}

