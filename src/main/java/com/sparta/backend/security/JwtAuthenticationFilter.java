package com.sparta.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.backend.user.dto.LoginRequestDto;
import com.sparta.backend.user.model.UserRoleEnum;
import com.sparta.backend.user.repository.RefreshTokenRepository;
import com.sparta.backend.user.repository.UserRepository;
import com.sparta.backend.workspace.exception.CustomException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtUtil jwtUtil;

    private final RefreshTokenRepository refreshTokenRepository;

    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        setFilterProcessesUrl("/api/user/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            LoginRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDto.getEmail(),
                            requestDto.getPassword(),
                            null
                    )
            );
        } catch (IOException e) {
            setFailureResponse(response, "로그인 실패");
            throw new CustomException(HttpStatus.NOT_FOUND, "Bad Request", "없거나 잘못된 Email 또는 비밀번호입니다.");
        }
    }

    private void setFailureResponse(HttpServletResponse response, String message) {
        try {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"" + message + "\"}");
        } catch (IOException e) {
            // 예외가 발생해도 추가 처리는 하지 않음
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {
        String email = ((UserDetailsImpl) authResult.getPrincipal()).getEmail();
        UserRoleEnum role = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getRole();

        String accessToken = jwtUtil.createAccessToken(email, role);

        String refreshToken = jwtUtil.createRefreshToken(accessToken);
        refreshTokenRepository.save(email, refreshToken);

        // 응답 본문에 JSON 작성
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        StringBuilder jsonBuilder = new StringBuilder();

        try {
            jsonBuilder.append("{");
            jsonBuilder.append("\"accessToken\": \"" + accessToken + "\",\n");
            jsonBuilder.append("\"id\": \"" + userRepository.findByEmail(email).get().getId().toString() + "\",\n");
            jsonBuilder.append("\"username\": \"" + userRepository.findByEmail(email).get().getUsername().toString() + "\",\n");
            jsonBuilder.append("\"email\": \"" + email + "\"");
            jsonBuilder.append("}");

            response.getWriter().write(jsonBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        response.setStatus(401);
    }
}