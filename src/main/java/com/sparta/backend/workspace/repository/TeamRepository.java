package com.sparta.backend.workspace.repository;

import com.sparta.backend.user.model.User;
import com.sparta.backend.workspace.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findByTeamIdAndIsDeleteFalse(Long id);
    List<Team> findAllByCreatorIdAndIsDeleteFalse(Long id);

}
