package com.sparta.backend.workspace.repository;

import com.sparta.backend.user.model.User;
import com.sparta.backend.workspace.entity.Team;
import com.sparta.backend.workspace.entity.TeamUser;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamUserRepository extends JpaRepository<TeamUser, Long> {

    @Transactional
    void deleteByTeamAndUser(Team team, User user);

    TeamUser findByTeamAndUser(Team team, User user);   //정의된 순서에 따라 호출 해야함.

    boolean existsByTeamAndUser(Team team, User user);

    Optional<TeamUser> findByTeamAndRole(Team team, String role); // 역할에 따라 팀 유저를 찾는 메서드 추가

    List<TeamUser> findByTeam(Team team);

    @Transactional
    void deleteByTeam(Team team);
}
