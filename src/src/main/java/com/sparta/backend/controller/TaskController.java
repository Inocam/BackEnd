package com.sparta.backend.controller;
// 클라이언트에서 받은 요청을 서비스에게 전달하는 방법

import com.sparta.backend.dto.TaskRequestDto;
import com.sparta.backend.dto.TaskResponseDto;
import com.sparta.backend.entity.Task;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController //html 아닌 "AJAX"로 통신하기 때문에 선언
@RequestMapping("/foot") //중복되는 경로를 줄여줌

public class TaskController {

    private final Map<Long, Task> taskList = new HashMap<>(); // 공부해야 함

    /* 생성 */
    @PostMapping("/task")
    public TaskResponseDto createTask(@RequestBody TaskRequestDto requestDto) {

        // 요청 requestDto -> 저장 entity
        Task task = new Task(requestDto); // 데이터가 들어있는 requestDto 객체를 바로 입력

        // Max ID 확인 : DB에 얼만큼의 데이터가 있는지 확인 -> 마지막 값 +1
        Long maxId = taskList.size() > 0 ? Collections.max(taskList.keySet()) + 1 : 1;
        task.setTaskId(maxId);

        // DB 저장
        taskList.put(task.getTaskId(), task);

        // entity -> responseDto
        TaskResponseDto taskResponseDto = new TaskResponseDto(task);

        return taskResponseDto;
    }
    /* 조회 */
    @GetMapping("/task")
    public List<TaskResponseDto> getTask() {
        // map 형식을 list 형식으로 바꾸기
        List<TaskResponseDto> responseList = taskList.values().stream().map(TaskResponseDto::new).toList(); //map의 모든 내용을 가지고 와서 list로 변환
        return responseList;
    }

    /* 수정 */
    @PutMapping("/task/{taskId}")
    public Long updateTask(@PathVariable Long taskId, @RequestBody TaskRequestDto requestDto) {
        // 해당 일정이 DB에 존재하는지 확인
        if (taskList.containsKey(taskId)) {
            // 해당 일정id 가져오기
            Task task = taskList.get(taskId);

            // memo 수정
            task.updateTask(requestDto);
            return task.getTaskId();
        } else {
            throw new IllegalArgumentException("수정-선택한 일정은 존재하지 않습니다.");
        }
    }

    /* 삭제 */
    @DeleteMapping("/task/{taskId}")
    public Long deleteTask(@PathVariable Long taskId) {
        // 해당 일정이 DB에 존재하는지 확인
        if (taskList.containsKey(taskId)) {
            // 해당 일정id 삭제하기
            taskList.remove(taskId);
            return taskId;
        } else {
            throw new IllegalArgumentException("삭제-선택한 일정은 존재하지 않습니다.");
        }
    }

}