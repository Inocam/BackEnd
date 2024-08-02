package com.sparta.backend.dto;
// 클라이언트로 나가는 요청

import com.sparta.backend.entity.Task;
import lombok.Getter;

@Getter

public class TaskResponseDto {
    private Long taskId;        //일정 key -자동생성
    private Long teamId;        //팀 key
    private String title;       //일정제목  -varchar(255)
    private String description; //일정상세 -text
    private String status;      //진행상황 -enum
    private String startDate;   //시작일자 -datetime
    private String dueDate;     //마감일자 -datetime
    private Long parentTask;    //상위일정

    public TaskResponseDto(Task task) {
        this.taskId = task.getTaskId();
        this.teamId = task.getTeamId();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.status = task.getStatus();
        this.startDate = task.getStartDate();
        this.dueDate = task.getDueDate();
        this.parentTask = task.getParentTask();
    }
}