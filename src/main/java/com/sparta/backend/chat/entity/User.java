package com.sparta.backend.chat.entity;

import com.sparta.backend.chat.dto.user.UserRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "User")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name="userName", nullable = false)
    private String userName;

    public User(UserRequestDto requestDto) {
        this.userName = requestDto.getUserName();
    }


}
