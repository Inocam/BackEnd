package com.sparta.backend.task.service;
// 컨트롤러에서 전달된 데이터를 처리하는 방법

import com.sparta.backend.task.dto.TaskRequestDto;
import com.sparta.backend.task.dto.TaskResponseDto;
import com.sparta.backend.task.dto.TaskUpdateResponseDto;
import com.sparta.backend.task.entity.Task;
import com.sparta.backend.task.repository.MainviewRepository;
import com.sparta.backend.task.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final MainviewRepository mainviewRepository;

    public TaskService(TaskRepository taskRepository, MainviewRepository mainviewRepository) {
        this.taskRepository = taskRepository;
        this.mainviewRepository = mainviewRepository;
    }

    /* 생성 */
    public TaskResponseDto createTask(TaskRequestDto requestDto) {
        // RequestDto -> Entity
        Task task = new Task(requestDto);

        // DB 저장
        Task saveTask = taskRepository.save(task);

        // Entity -> ResponseDto
        TaskResponseDto taskResponseDto = new TaskResponseDto(saveTask);

        return taskResponseDto;
    }

    /* 조회 */
    // 전체 조회
    public List<TaskResponseDto> getTask() {
        // DB 조회
        return taskRepository.findAll().stream().map(TaskResponseDto::new).toList();
    }

    // 특정 월의 팀별 일자별 task 갯수
    public Map<String, Long> countTasksByDay(String startDate, String endDate, Long teamId) {
        List<Object[]> results = mainviewRepository.countTasksByDay(startDate, endDate, teamId);
        Map<String, Long> taskCountByDay = new LinkedHashMap<>();

        for (Object[] result : results) {
            String dueDate = (String) result[0];
            Long count = (Long) result[1];
            taskCountByDay.put(dueDate, count);
        }

        return taskCountByDay;
    }

    // 특정 월의 팀별 상태별 task 갯수
    public Map<String, Long> countTasksByStatus(String startDate, String endDate, Long teamId) {
        List<Object[]> results = mainviewRepository.countTasksByStatus(startDate, endDate, teamId);
        Map<String, Long> taskCountByStatus = new LinkedHashMap<>();

        for (Object[] result : results) {
            String status = (String) result[0];
            Long count = (Long) result[1];
            taskCountByStatus.put(status, count);
        }

        return taskCountByStatus;
    }

    // 특정 날짜의 팀별 일정 조회
    public List<TaskResponseDto> getTaskByDueDate(String dueDate, Long teamId) {
        return taskRepository.findAllByDueDateAndTeamId(dueDate, teamId).stream().map(TaskResponseDto::new).toList();
    }


    /* 수정 */
    @Transactional //영속성 -> 변경추적
    public TaskUpdateResponseDto updateTask(Long taskId, TaskRequestDto requestDto) {
        // 해당 메모가 DB에 존재하는지 확인
        Task task = findTask(taskId);

        String beforeDueDate = task.getDueDate();

        // task 내용 수정
        task.update(requestDto);

        TaskResponseDto taskResponseDto = new TaskResponseDto(task);
        taskResponseDto.setType("update");

        TaskUpdateResponseDto taskUpdateResponseDto = new TaskUpdateResponseDto(taskResponseDto, beforeDueDate);

        return taskUpdateResponseDto;
    }

    /* 삭제 */
    public Long deleteTaskAndReturnTeamId(Long taskId) {
        // 해당 메모가 DB에 존재하는지 확인
        Task task = findTask(taskId);

        Long teamId = task.getTeamId();

        // task 삭제
        taskRepository.delete(task);

        return teamId;
    }

    private Task findTask(Long TaskId) {
        return taskRepository.findById(TaskId).orElseThrow(() ->
                new IllegalArgumentException("선택한 일정은 존재하지 않습니다.")
        );
    }
}