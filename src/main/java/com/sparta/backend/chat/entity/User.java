package com.sparta.backend.chat.entity;

import com.sparta.backend.chat.dto.user.UserRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name="senderId", unique = true, nullable = false)
    private Long senderId;

    @Column(name="userName", nullable = false)
    private String userName;

    public User(UserRequestDto requestDto) {
        this.userName = requestDto.getUserName();
        this.senderId = requestDto.getSenderId();
    }
}

