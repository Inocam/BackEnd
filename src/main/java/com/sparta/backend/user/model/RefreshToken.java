package com.sparta.backend.user.model;

import com.sparta.backend.security.JwtUtil;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

import java.util.Date;

@Entity
@Getter
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String token;
    private Date expirationDate;

    public RefreshToken(String username, String refreshToken) {
        this.username = username;
        this.token = refreshToken;
        this.expirationDate = new Date(System.currentTimeMillis() + JwtUtil.REFRESH_TOKEN_EXPIRATION);
    }
}