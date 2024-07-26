package com.sparta.backend.chat.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "ChatRoom")
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    @Column(name="roomName", nullable = false)
    private String roomName;

    @Column(name="createdDate", nullable = false)
    private LocalDateTime createdDate;

    @ManyToOne
    @JoinColumn(name="userId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "senderId", nullable = false)
    private User sender;

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public void setCreatedDate(LocalDateTime now) {
        this.createdDate = now;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

}
