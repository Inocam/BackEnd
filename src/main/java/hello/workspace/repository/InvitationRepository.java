package hello.workspace.repository;

import hello.workspace.entity.Invitation;
import hello.workspace.entity.Team;
import hello.workspace.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    List<Invitation> findByUserIdAndStatus(Long Id, String status);
    List<Invitation> findByUserId(Long userId); // 사용자 ID로 모든 초대 조회

    boolean existsByUserAndTeam(User user, Team team);
}

