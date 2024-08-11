package com.sparta.backend.task.repository;
// DB와 주고받는 데이터를 처리하는 방법을 정의

import com.sparta.backend.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MainviewRepository extends JpaRepository<Task, Long> {

    // 메인화면 -> 선택한 월 -> 1일~말일 팀별 일자별 task 갯수
    @Query("SELECT t.dueDate, COUNT(t.taskId) " +
            "FROM Task t WHERE STR_TO_DATE(t.dueDate, '%Y-%m-%d') BETWEEN STR_TO_DATE(:startDate, '%Y-%m-%d') AND STR_TO_DATE(:endDate, '%Y-%m-%d') " +
            "AND t.teamId = :teamId GROUP BY t.dueDate ORDER BY t.dueDate")
    List<Object[]> countTasksByDay(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("teamId") Long teamId);

    // 메인화면 -> 선택한 월 -> 1일~말일 팀별 status 갯수
    @Query("SELECT t.status, COUNT(t.taskId) " +
            "FROM Task t WHERE STR_TO_DATE(t.dueDate, '%Y-%m-%d') BETWEEN STR_TO_DATE(:startDate, '%Y-%m-%d') AND STR_TO_DATE(:endDate, '%Y-%m-%d') " +
            "AND t.teamId = :teamId GROUP BY t.status ORDER BY t.status")
    List<Object[]> countTasksByStatus(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("teamId") Long teamId);
}
