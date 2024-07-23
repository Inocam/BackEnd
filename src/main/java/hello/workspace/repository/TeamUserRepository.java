package hello.workspace.repository;

import hello.workspace.entity.Team;
import hello.workspace.entity.TeamUser;
import hello.workspace.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamUserRepository extends JpaRepository<TeamUser, Long> {

    @Transactional
    void deleteByTeamAndUser(Team team, User user);
}
