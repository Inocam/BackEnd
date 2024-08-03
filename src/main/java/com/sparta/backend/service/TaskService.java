package com.sparta.backend.service;
// 컨트롤러에서 전달된 데이터를 처리하는 방법

import com.sparta.backend.dto.TaskRequestDto;
import com.sparta.backend.dto.TaskResponseDto;
import com.sparta.backend.entity.Task;
import com.sparta.backend.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public TaskResponseDto createTask(TaskRequestDto requestDto) {
        // RequestDto -> Entity
        Task task = new Task(requestDto);

        // DB 저장
        Task saveTask = taskRepository.save(task);

        // Entity -> ResponseDto
        TaskResponseDto taskResponseDto = new TaskResponseDto(saveTask);

        return taskResponseDto;
    }

    public List<TaskResponseDto> getTask() {
        // DB 조회
        return taskRepository.findAll().stream().map(TaskResponseDto::new).toList();
    }

    @Transactional //영속성 -> 변경추적
    public Long updateTask(Long taskId, TaskRequestDto requestDto) {
        // 해당 메모가 DB에 존재하는지 확인
        Task task = findTask(taskId);

        // task 내용 수정
        task.update(requestDto);

        return taskId;
    }

    public Long deleteTask(Long taskId) {
        // 해당 메모가 DB에 존재하는지 확인
        Task task = findTask(taskId);

        // task 삭제
        taskRepository.delete(task);

        return taskId;
    }

    private Task findTask(Long TaskId) {
        return taskRepository.findById(TaskId).orElseThrow(() ->
                new IllegalArgumentException("선택한 메모는 존재하지 않습니다.")
        );
    }
}