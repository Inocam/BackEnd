package com.sparta.backend.chat.controller;

import com.sparta.backend.chat.dto.user.UserRequestDto;
import com.sparta.backend.chat.dto.user.UserResponseDto;
import com.sparta.backend.chat.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/foot")
public class UserController {

    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }

    // user 조회
    @GetMapping("/users")
    public List<UserResponseDto> getUsers() {
        return userService.getUsers();
    }

    // user 등록
    @PostMapping("/users")
    public UserResponseDto createUser(@RequestBody UserRequestDto requestDto) {
        return userService.createUser(requestDto);
    }
}
