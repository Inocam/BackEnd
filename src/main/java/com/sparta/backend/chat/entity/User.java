package com.sparta.backend.chat.entity;

import com.sparta.backend.chat.dto.user.UserRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name="user_name", nullable = false)
    private String userName;

    @OneToMany(mappedBy = "user")
    private List<UserRoom> userRooms = new ArrayList<>();

    public User(UserRequestDto requestDto) {
        this.userName = requestDto.getUserName();
    }
}

