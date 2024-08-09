package com.sparta.backend.task.repository;
// DB와 주고받는 데이터를 처리하는 방법을 정의

import com.sparta.backend.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MainviewRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t.dueDate, COUNT(t.taskId) " +
            "FROM Task t WHERE STR_TO_DATE(t.dueDate, '%Y-%m-%d') BETWEEN STR_TO_DATE(:startDate, '%Y-%m-%d') AND STR_TO_DATE(:endDate, '%Y-%m-%d') " +
            "GROUP BY t.dueDate ORDER BY t.dueDate")
    List<Object[]> countTasksByDay(@Param("startDate") String startDate, @Param("endDate") String endDate);
}
