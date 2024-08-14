package com.sparta.backend.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.backend.user.dto.UserResponseDto;
import com.sparta.backend.security.JwtUtil;
import com.sparta.backend.security.UserDetailsImpl;
import com.sparta.backend.user.service.KakaoService;
import com.sparta.backend.user.service.UserService;
import com.sparta.backend.user.dto.SignupRequestDto;
import com.sparta.backend.user.dto.UserInfoDto;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final KakaoService kakaoService;
    private final JwtUtil jwtUtil;


    @PostMapping("/user/signup")
    public ResponseEntity<String> signup(@RequestBody @Valid SignupRequestDto requestDto, BindingResult bindingResult) {
        // Validation 예외처리
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        if (fieldErrors.size() > 0) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                log.error(fieldError.getField() + " 필드 : " + fieldError.getDefaultMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("not valid input");
        }

        try {
            userService.signup(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body("signup success");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("회원가입 중 중복 발생");
        }
    }

    // 회원 정보 받기
    @GetMapping("/user-info")
    @ResponseBody
    public UserInfoDto getUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        String username = userDetails.getUser().getUsername();
        String email = userDetails.getUser().getEmail();
        Long userId = userDetails.getUser().getId();

        return new UserInfoDto(userId, username, email);
    }

    @GetMapping("/user/kakao/callback")
    public String kakaoCallback(@RequestParam String code, HttpServletResponse response, @AuthenticationPrincipal UserDetailsImpl userDetails) throws JsonProcessingException {
        kakaoService.kakaoLogin(code, response);

        return "redirect:/";
    }

    @GetMapping("/users")
    public List<UserResponseDto> getUsersByUsernamePrefix(@RequestParam String prefix) {
        return userService.getUsersByUsernamePrefix(prefix);
    }
}