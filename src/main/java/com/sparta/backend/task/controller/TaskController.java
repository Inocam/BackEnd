package com.sparta.backend.task.controller;
// 클라이언트에서 받은 요청을 서비스에게 전달하는 방법

import com.sparta.backend.task.dto.MainviewResponseDto;
import com.sparta.backend.task.dto.TaskRequestDto;
import com.sparta.backend.task.dto.TaskResponseDto;
import com.sparta.backend.task.service.TaskService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController //html 아닌 "AJAX"로 통신하기 때문에 선언
@RequestMapping("/foot/task") //중복되는 경로를 줄여줌

public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /* 생성 */
    @PostMapping("/create")
    public TaskResponseDto createTask(@RequestBody TaskRequestDto requestDto) { //컨트롤러의 매서드 이름 == 서비스의 매서드 이름 : 알아보기 쉬움
        return taskService.createTask(requestDto); //컨트롤러의 매서드 이름 == 서비스의 매서드 이름 : 알아보기 쉬움
    }


    /* 조회 */
    // 전체조회
    @GetMapping("")
    public List<TaskResponseDto> getTask() {
        return taskService.getTask();
    }

    // 메인화면 -> 선택 월 (1일~말일) -> 일자별 task 갯수
    @GetMapping("/mainview/countTask")
    public Map<String, Long> countTasksByDay(@RequestParam String startDate, @RequestParam String endDate) {
        return taskService.countTasksByDay(startDate, endDate);
    }

    // 메인화면 -> 선택 월 (1일~말일) -> 전체 status 갯수
    @GetMapping("/mainview/countTaskStatus")
    public Map<String, Long> countTasksByStatus(@RequestParam String startDate, @RequestParam String endDate) {
        return taskService.countTasksByStatus(startDate, endDate);
    }

    // 메인화면 -> 선택 월 -> 선택 일 -> task list
    @GetMapping("/mainview/")
    public List<MainviewResponseDto> getView(@RequestParam("dueDate") String dueDate) {
        return taskService.getView(dueDate);
    }


    /* 수정 */
    @PutMapping("/update/{taskId}")
    public Long updateTask(@PathVariable Long taskId, @RequestBody TaskRequestDto requestDto) {
        return taskService.updateTask(taskId, requestDto);
    }


    /* 삭제 */
    @DeleteMapping("/delete/{taskId}")
    public Long deleteTask(@PathVariable Long taskId) {
        return taskService.deleteTask(taskId);
    }

}