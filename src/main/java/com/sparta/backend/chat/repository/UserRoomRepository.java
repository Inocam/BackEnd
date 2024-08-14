package com.sparta.backend.chat.repository;

import com.sparta.backend.chat.entity.ChatRoom;
import com.sparta.backend.chat.entity.UserRoom;
import com.sparta.backend.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRoomRepository extends JpaRepository<UserRoom, Long> {

    boolean existsByChatRoomAndUser(ChatRoom chatRoom, User user);

    Page<UserRoom> findByUserId(Long userId, Pageable pageable);

    Optional<List<UserRoom>> findALlByChatRoom(ChatRoom chatRoom);
}
