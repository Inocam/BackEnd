package com.sparta.backend.user.repository;

import com.sparta.backend.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    Optional<User> findByKakaoId(Long kakaoId);
    List<User> findAllByEmailStartingWith(String prefix);

    Optional<User> findByIdAndIsDeleteFalse(Long id);

}