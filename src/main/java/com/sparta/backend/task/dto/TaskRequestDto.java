package com.sparta.backend.task.dto;
// 클라이언트에서 들어오는 요청

import lombok.Getter;

@Getter

public class TaskRequestDto {
    private Long taskId;      //일정 key -자동생성
    private Long teamId;        //팀 key
    private Long userId;        //작성자 key
    private String title;       //일정제목  -varchar(255)
    private String description; //일정상세 -text
    private String status;      //진행상황 -enum
    private String startDate;   //시작일자 -date
    private String dueDate;     //마감일자 -date
    private String endDate;     //완료일자 -date
    private Long parentTask;    //상위일정
}
