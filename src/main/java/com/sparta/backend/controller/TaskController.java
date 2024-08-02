package com.sparta.backend.controller;

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

    private final Map<Long, Task> memoList = new HashMap<>(); // 공부해야 함

    @PostMapping("/task") // 신규생성
    public TaskResponseDto createMemo(@RequestBody TaskRequestDto requestDto) {

        // 요청 requestDto -> 저장 entity
        Task task = new Task(requestDto); // 데이터가 들어있는 requestDto 객체를 바로 입력

        // Max ID 확인 : DB에 얼만큼의 데이터가 있는지 확인 -> 마지막 값 +1
        Long maxId = memoList.size() > 0 ? Collections.max(memoList.keySet()) + 1 : 1;
        task.setTaskId(maxId);

        // DB 저장
        memoList.put(task.getTaskId(), task);

        // entity -> responseDto
        TaskResponseDto taskResponseDto = new TaskResponseDto(task);

        return taskResponseDto;
    }

    @GetMapping("/task")
    public List<TaskResponseDto> getMemo() {
        // map 형식을 list 형식으로 바꾸기
        List<TaskResponseDto> responseList = memoList.values().stream().map(TaskResponseDto::new).toList(); //map의 모든 내용을 가지고 와서 list로 변환
        return responseList;
    }

}