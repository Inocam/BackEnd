package com.sparta.backend.task.dto;

import lombok.Getter;

@Getter
public class TaskDeleteResponseDto {
    Long teamId;
    Long taskId;
    String dueDate;
    String status;

    public TaskDeleteResponseDto( Long teamId, Long taskId, String dueDate, String status) {
        this.teamId = teamId;
        this.taskId = taskId;
        this.dueDate = dueDate;
        this.status = status;
    }
}
