package com.sparta.backend.chat.entity;

import com.sparta.backend.chat.dto.chatRoom.ChatRoomRequestDto;
import com.sparta.backend.user.model.User;
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

    @Column(name="room_name", nullable = false)
    private String roomName;

    @Column(name="created_Date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name="is_deleted", nullable = false)
    private boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // userId 추가
    private User user;

    @OneToMany(mappedBy = "chatRoom")
    private List<UserRoom> userRooms = new ArrayList<>();

    public ChatRoom(ChatRoomRequestDto chatRoomRequestDto, User user) {
        this.roomName = chatRoomRequestDto.getRoomName();
        this.user = user;
        this.createdDate = LocalDateTime.now();
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}

