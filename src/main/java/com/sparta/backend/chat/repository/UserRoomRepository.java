package com.sparta.backend.chat.repository;

import com.sparta.backend.chat.entity.ChatRoom;
import com.sparta.backend.chat.entity.UserRoom;
import com.sparta.backend.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRoomRepository extends JpaRepository<UserRoom, Long> {
    List<UserRoom> findAllByUserId(Long userId);

    boolean existsByChatRoomAndUser(ChatRoom chatRoom, User user);
}
