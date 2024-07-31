package com.sparta.backend.chat.entity;

import com.sparta.backend.chat.dto.chatRoom.ChatRoomRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "chat_rooms")
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    @Column(name="roomName", nullable = false)
    private String roomName;

    @Column(name="createdDate", nullable = false)
    private LocalDateTime createdDate;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false) // userId 추가
    private User user;

    @OneToMany(mappedBy = "chatRoom")
    private List<UserRoom> userRooms = new ArrayList<>();

    public ChatRoom(ChatRoomRequestDto chatRoomRequestDto, User user) {
        this.roomName = chatRoomRequestDto.getRoomName();
        this.user = user;
        this.createdDate = LocalDateTime.now();
    }

}

