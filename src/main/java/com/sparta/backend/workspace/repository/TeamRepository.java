package com.sparta.backend.workspace.repository;

import com.sparta.backend.workspace.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
