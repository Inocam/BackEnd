package com.sparta.backend.task.dto;
// 클라이언트에서 들어오는 요청

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class MainviewRequestDto {
    private String dueDate;     //마감일자 -date

    public MainviewRequestDto(String dueDate) {
        this.dueDate = dueDate;
    }
}
