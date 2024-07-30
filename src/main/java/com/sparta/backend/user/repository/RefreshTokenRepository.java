package com.sparta.backend.user.repository;

import com.sparta.backend.user.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    RefreshToken findByUsername(String username);
}