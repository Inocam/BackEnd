package com.sparta.backend.chat.entity;

import com.sparta.backend.chat.dto.chatRoom.ChatRoomRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "chatRooms")
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

    public ChatRoom(ChatRoomRequestDto chatRoomRequestDto, User user, User sender) {
        this.roomName = chatRoomRequestDto.getRoomName();
        this.createdDate = LocalDateTime.now();
        this.user = user;
        this.sender = sender;
    }
}

