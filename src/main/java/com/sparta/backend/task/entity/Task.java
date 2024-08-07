package com.sparta.backend.task.entity;

/* 포함 라이브러리, 패키지,  */

import com.sparta.backend.task.dto.TaskRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/* 사용 어노테이션 선언 */
@Entity // 해당 클래스가 entity 임을 지정
@Getter // Lombok 라이브러리 - getter 메서드 자동생성
@Setter // Lombok 라이브러리 - setter 메서드 자동생성
@Table(name = "task")   // 아래 규칙을 사용한 DB의 table name 지정
@NoArgsConstructor      // Lombok 라이브러리 - 기본 생성자를 자동 생성

//public enum Status {
//    todooo,
//    ongoing,
//    done,
//    delay
//} enum 사용방법은 일단 crud 하고나서!!

public class Task {
    /* 일정 key */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskId;

    /* 팀 key <- team table 참조 */
    @Column(name = "teamId", nullable = false, length = 100)
    private Long teamId;

    /* 작성자 key <- user table 참조 */
    @Column(name = "userId", nullable = false, length = 100)
    private Long userId;

    /* 일정제목  -varchar(255) */
    @Column(name = "title", nullable = false, length = 255, columnDefinition = "VARCHAR(255)")
    private String title;

    /* 일정제목 */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /* 진행상황 -enum */
//    @Enumerated(EnumType.ORDINAL)
//    @Column(nullable = false)
// enum 사용방법은 일단 crud 하고나서!!
    @Column(name = "status", nullable = false, length = 100, columnDefinition = "VARCHAR(100)")
    private String status;

    /* 시작일자 -date */
    @Column(name = "startDate", nullable = false, length = 100, columnDefinition = "VARCHAR(100)")
    private String startDate;

    /* 마감일자 -date */
    @Column(name = "dueDate", nullable = false, length = 100, columnDefinition = "VARCHAR(100)")
    private String dueDate;

    /* 완료일자 -date */
    @Column(name = "endDate", nullable = false, length = 100, columnDefinition = "VARCHAR(100)")
    private String endDate;

    /* 상위일정 key */
    @Column(name = "parentTask", nullable = true, length = 100)
    private Long parentTask;


    public Task(TaskRequestDto requestDto) { // 클라이언트에서 받아온 데이터가 requestDto 에 들어있음
        this.teamId = requestDto.getTeamId();
        this.userId = requestDto.getUserId();
        this.title = requestDto.getTitle();
        this.description = requestDto.getDescription();
        this.status = requestDto.getStatus();
        this.startDate = requestDto.getStartDate();
        this.dueDate = requestDto.getDueDate();
        this.endDate = requestDto.getEndDate();
        this.parentTask = requestDto.getParentTask();
    }

    public void update(TaskRequestDto requestDto) {
        this.teamId = requestDto.getTeamId();
        this.userId = requestDto.getUserId();
        this.title = requestDto.getTitle();
        this.description = requestDto.getDescription();
        this.status = requestDto.getStatus();
        this.startDate = requestDto.getStartDate();
        this.dueDate = requestDto.getDueDate();
        this.endDate = requestDto.getEndDate();
        this.parentTask = requestDto.getParentTask();
    }

}