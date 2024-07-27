package com.sparta.backend.chat.repository;

import com.sparta.backend.chat.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findBySenderId(Long senderId);
}
