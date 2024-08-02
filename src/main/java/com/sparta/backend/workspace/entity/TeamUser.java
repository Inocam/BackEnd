package com.sparta.backend.workspace.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    public TeamUser(User user, Team team, String role) {    //팀장,팀원 구분 role로 함
        this.user = user;
        this.team = team;
        this.role = role;
    }
}
