package hello.workspace.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "invitation")
@NoArgsConstructor
@Getter
@Setter
public class Invitation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String status;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "requester_id", nullable = false)
    private Long requesterId;

    //팀과 유저를 서비스나, 컨트롤러에서 조회 후 주입하는 생성자
    public Invitation(String status, Team team, User user, Long requesterId) {
        this.status = status;
        this.team = team;
        this.user = user;
        this.requesterId = requesterId;
    }
}
