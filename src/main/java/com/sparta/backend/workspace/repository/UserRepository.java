package com.sparta.backend.workspace.repository;

import com.sparta.backend.workspace.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {
}
