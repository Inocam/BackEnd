package com.sparta.backend.chat.repository;

import com.sparta.backend.chat.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
