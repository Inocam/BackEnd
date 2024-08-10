package com.sparta.backend.task.dto;
// 클라이언트로 나가는 요청

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class MainviewResponseDto {
    private Long taskId;        //일정 key -자동생성
    private String dueDate;     //마감일자 -date
    private String title;       //일정제목  -varchar(255)
    private String description; //일정상세 -text
    private String status;      //진행상황 -enum

//    private Long teamId;        //팀 key
//    private Long userId;        //작성자 key
//    private String startDate;   //시작일자 -date
//    private String endDate;     //완료일자 -date
//    private Long parentTask;    //상위일정

    public MainviewResponseDto(long taskId, String dueDate, String title, String description, String status) {
        this.taskId = taskId;
        this.dueDate = dueDate;
        this.title = title;
        this.description = description;
        this.status = status;
    }

}


