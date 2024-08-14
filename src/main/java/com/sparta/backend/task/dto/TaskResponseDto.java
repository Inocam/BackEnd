package com.sparta.backend.task.dto;
// 클라이언트로 나가는 요청

import com.sparta.backend.task.entity.Task;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class TaskResponseDto {
    private String type;
    private Long taskId;        //일정 key -자동생성
    private Long teamId;        //팀 key
    private Long userId;        //작성자 key
    private String title;       //일정제목  -varchar(255)
    private String description; //일정상세 -text
    private String status;      //진행상황 -enum
    private String startDate;   //시작일자 -date
    private String dueDate;     //마감일자 -date
    private String endDate;     //완료일자 -date
    private Long parentTask;    //상위일정

    public TaskResponseDto(Task task) {
        this.taskId = task.getTaskId();
        this.teamId = task.getTeamId();
        this.userId = task.getUserId();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.status = task.getStatus();
        this.startDate = task.getStartDate();
        this.dueDate = task.getDueDate();
        this.endDate = task.getEndDate();
        this.parentTask = task.getParentTask();
    }
}