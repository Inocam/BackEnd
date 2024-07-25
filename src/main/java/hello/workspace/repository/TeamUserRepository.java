package hello.workspace.repository;

import hello.workspace.entity.Team;
import hello.workspace.entity.TeamUser;
import hello.workspace.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamUserRepository extends JpaRepository<TeamUser, Long> {

    @Transactional
    void deleteByTeamAndUser(Team team, User user);

    TeamUser findByTeamAndUser(Team team, User user);   //정의된 순서에 따라 호출 해야함.

    boolean existsByTeamAndUser(Team team, User user);

    @Transactional
    void deleteByTeam(Team team);
}
