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
    private Long id;

    @Column(name="userName", nullable = false)
    private String userName;

    public User(UserRequestDto requestDto) {
        this.userName = requestDto.getUserName();
    }
}

