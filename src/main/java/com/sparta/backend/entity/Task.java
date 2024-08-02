package com.sparta.backend.entity;

import com.sparta.backend.dto.TaskRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class Task {
    private Long taskId;        //일정 key
    private Long teamId;        //팀 key
    private String title;       //일정제목  -varchar(255)
    private String description; //일정상세 -text
    private String status;      //진행상황 -enum
    private String startDate;   //시작일자 -datetime
    private String dueDate;     //마감일자 -datetime
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
}