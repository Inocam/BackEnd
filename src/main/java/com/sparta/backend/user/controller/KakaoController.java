package com.sparta.backend.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.backend.security.UserDetailsImpl;
import com.sparta.backend.user.service.KakaoService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/kakao/callback")
@RequiredArgsConstructor
public class KakaoController {
    private final KakaoService kakaoService;

    @GetMapping()
    public String kakaoCallback(@RequestParam String code, HttpServletResponse response, @AuthenticationPrincipal UserDetailsImpl userDetails) throws JsonProcessingException {
        kakaoService.kakaoLogin(code, response);

        return "redirect:/";
    }
}
