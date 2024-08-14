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

    // ================================ 등록 ================================

    /**
     * 새로운 일정을 생성합니다.
     *
     * @param requestDto 생성할 일정의 정보
     * @return 생성된 일정의 응답 정보
     */
    @PostMapping("/create")
    public TaskResponseDto createTask(@RequestBody TaskRequestDto requestDto) { //컨트롤러의 매서드 이름 == 서비스의 매서드 이름 : 알아보기 쉬움
        return taskService.createTask(requestDto); //컨트롤러의 매서드 이름 == 서비스의 매서드 이름 : 알아보기 쉬움
    }


    // ================================ 조회 ================================

    /**
     * DB내 모든 일정을 조회
     *
     * @return DB내 모든 일정의 리스트
     */
    @GetMapping("/read/taskList/all")
    public List<TaskResponseDto> getTask() {
        return taskService.getTask();
    }

    // 메인화면 -> 선택 월 (1일~말일) -> 일자별 task 갯수

    /**
     * 특정 기간 동안 팀별로 일자별 일정 수를 조회합니다.
     *
     * @param startDate 조회 시작일
     * @param endDate   조회 종료일
     * @param teamId    조회할 팀의 ID
     * @return 팀별 일자별 일정 수
     */
    @GetMapping("/read/taskListQuantity/MonthByTeam")
    public Map<String, Long> countTasksByDay(@RequestParam String startDate, @RequestParam String endDate, @RequestParam Long teamId) {
        return taskService.countTasksByDay(startDate, endDate, teamId);
    }

    // 메인화면 -> 선택 월 (1일~말일) -> 전체 status 갯수

    /**
     * 특정 기간 동안 팀별로 상태별 일정 수를 조회합니다.
     *
     * @param startDate 조회 시작일
     * @param endDate   조회 종료일
     * @param teamId    조회할 팀의 ID
     * @return 팀별 상태별 일정 수
     */
    @GetMapping("/read/taskStatusQuantity/MonthByTeam")
    public Map<String, Long> countTasksByStatus(@RequestParam String startDate, @RequestParam String endDate, @RequestParam Long teamId) {
        return taskService.countTasksByStatus(startDate, endDate, teamId);
    }

    // 메인화면 -> 선택 월 -> 선택 일 -> task list

    /**
     * 특정 날짜에 해당하는 팀의 일정을 조회합니다.
     *
     * @param dueDate 조회할 날짜
     * @param teamId  조회할 팀의 ID
     * @return 특정 날짜의 팀 일정
     */
    @GetMapping("/read/taskList/DayByTeam")
    public List<TaskResponseDto> getTaskByDueDate(@RequestParam String dueDate, @RequestParam Long teamId) {
        return taskService.getTaskByDueDate(dueDate, teamId);
    }


    // ================================ 수정 ================================

    /**
     * 특정 일정을 수정
     *
     * @param taskId     수정할 일정의 ID
     * @param requestDto 수정할 일정의 정보
     * @return 수정된 일정의 ID
     */
    @PutMapping("/update/{taskId}")
    public Long updateTask(@PathVariable Long taskId, @RequestBody TaskRequestDto requestDto) {
        return taskService.updateTask(taskId, requestDto);
    }


    // ================================ 삭제 ================================

    /**
     * 특정 일정을 삭제
     *
     * @param taskId 삭제할 일정의 ID
     * @return 삭제된 일정의 ID
     */
    @DeleteMapping("/delete/{taskId}")
    public Long deleteTask(@PathVariable Long taskId) {
        return taskService.deleteTask(taskId);
    }

}