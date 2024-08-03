package com.sparta.backend.controller;
// 클라이언트에서 받은 요청을 서비스에게 전달하는 방법

import com.sparta.backend.dto.TaskRequestDto;
import com.sparta.backend.dto.TaskResponseDto;
import com.sparta.backend.service.TaskService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController //html 아닌 "AJAX"로 통신하기 때문에 선언
@RequestMapping("/foot") //중복되는 경로를 줄여줌

public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /* 생성 */
    @PostMapping("/task")
    public TaskResponseDto createTask(@RequestBody TaskRequestDto requestDto) { //컨트롤러의 매서드 이름 == 서비스의 매서드 이름 : 알아보기 쉬움
        return taskService.createTask(requestDto); //컨트롤러의 매서드 이름 == 서비스의 매서드 이름 : 알아보기 쉬움
    }
    /* 조회 */
    @GetMapping("/task")
    public List<TaskResponseDto> getTask() {
        return taskService.getTask();
    }

    /* 수정 */
    @PutMapping("/task/{taskId}")
    public Long updateTask(@PathVariable Long taskId, @RequestBody TaskRequestDto requestDto) {
        return taskService.updateTask(taskId, requestDto);
    }

    /* 삭제 */
    @DeleteMapping("/task/{taskId}")
    public Long deleteTask(@PathVariable Long taskId) {
        return taskService.deleteTask(taskId);
    }

}