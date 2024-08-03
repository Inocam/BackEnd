package com.sparta.backend.entity;

import com.sparta.backend.dto.TaskRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity // JPA가 관리할 수 있는 Entity 클래스 지정
@Getter
@Setter
@Table(name = "task") // 매핑할 테이블의 이름을 지정
@NoArgsConstructor

//public enum Status {
//    todooo,
//    ongoing,
//    done,
//    delay
//} enum 사용방법은 일단 crud 하고나서!!

public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskId;        //일정 key

    @Column(name = "teamId", nullable = false, length = 100)
    private Long teamId;        //팀 key

    @Column(name = "title", nullable = false, length = 255, columnDefinition = "VARCHAR(255)")
    private String title;       //일정제목  -varchar(255)

    @Column(name = "description", columnDefinition = "TEXT")
    private String description; //일정상세 -text

//    @Enumerated(EnumType.ORDINAL)
//    @Column(nullable = false)
// enum 사용방법은 일단 crud 하고나서!!
    @Column(name = "status", nullable = false, length = 100, columnDefinition = "VARCHAR(100)")
    private String status;      //진행상황 -enum

    @Column(name = "startDate", nullable = false, length = 100, columnDefinition = "VARCHAR(100)")
    private String startDate;   //시작일자 -date

    @Column(name = "dueDate", nullable = false, length = 100, columnDefinition = "VARCHAR(100)")
    private String dueDate;     //마감일자 -date

    @Column(name = "parentTask", nullable = true, length = 100)
    private Long parentTask;    //상위일정


    public Task(TaskRequestDto requestDto) { // 클라이언트에서 받아온 데이터가 requestDto 에 들어있음
        this.teamId = requestDto.getTeamId(); // ????? 이부분을 어떻게 받아오지?
        this.title = requestDto.getTitle();
        this.description = requestDto.getDescription();
        this.status = requestDto.getStatus();
        this.startDate = requestDto.getStartDate();
        this.dueDate = requestDto.getDueDate();
        this.parentTask = requestDto.getParentTask();
    }

    public void update(TaskRequestDto requestDto) {
        this.teamId = requestDto.getTeamId(); // ????? 이부분을 어떻게 받아오지?
        this.title = requestDto.getTitle();
        this.description = requestDto.getDescription();
        this.status = requestDto.getStatus();
        this.startDate = requestDto.getStartDate();
        this.dueDate = requestDto.getDueDate();
        this.parentTask = requestDto.getParentTask();
    }

}