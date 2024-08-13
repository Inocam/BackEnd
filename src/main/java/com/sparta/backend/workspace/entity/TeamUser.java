package com.sparta.backend.workspace.entity;

import com.sparta.backend.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "Teamuser")
@NoArgsConstructor
@Getter
@Setter
public class TeamUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) //
    private User user;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(nullable = false)
    private String role;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt; //팀에 합류한 시간 = 초대 수락한 시간

    public TeamUser(User user, Team team, String role) {    //팀장,팀원 구분 role로 함
        this.user = user;
        this.team = team;
        this.role = role;
        this.joinedAt = LocalDateTime.now();
    }
}
