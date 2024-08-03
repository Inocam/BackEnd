package com.sparta.backend.task.repository;
// DB와 주고받는 데이터를 처리하는 방법을 정의

import com.sparta.backend.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

//    List<Task> findAllByOrderByTaskId(); //이름패턴으로 매서드선언하면 SQL문법 안쓰고 바로 이용가능

//    List<Memo> findAllByUsername(String username); //sql을 동적으로 이용 가능

}