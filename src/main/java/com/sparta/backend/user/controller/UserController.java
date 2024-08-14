package com.sparta.backend.user.controller;

import com.sparta.backend.user.dto.UserResponseDto;
import com.sparta.backend.user.service.UserService;
import com.sparta.backend.user.dto.SignupRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @ResponseBody
    @PostMapping("/user/signup")
    public ResponseEntity<String> signup(@RequestBody @Valid SignupRequestDto requestDto) {
        return userService.signup(requestDto);
    }

    @ResponseBody
    @GetMapping("/user/refresh")
    public void refresh(HttpServletRequest request, HttpServletResponse response) {
        userService.refresh(request, response);
    }

    @ResponseBody
    @GetMapping("/users")
    public List<UserResponseDto> getUsersByEmailPrefix(@RequestParam String prefix) {
        return userService.getUsersByEmailPrefix(prefix);
    }

    @ResponseBody
    @GetMapping("/read/user/{userId}")
    public UserResponseDto getUserNameAndEmailById(@PathVariable Long userId) {
        return userService.getUserNameAndEmailById(userId);
    }

}