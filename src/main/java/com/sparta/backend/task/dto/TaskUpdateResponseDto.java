package com.sparta.backend.task.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TaskUpdateResponseDto {
    TaskResponseDto taskResponseDto;
    String beforeDueDate;
    String beforeStatus;
}
