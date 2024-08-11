package com.sparta.backend.user.service;

import com.google.gson.Gson;
import com.sparta.backend.security.JwtUtil;
import com.sparta.backend.user.dto.SignupRequestDto;
import com.sparta.backend.user.dto.UserResponseDto;
import com.sparta.backend.user.model.User;
import com.sparta.backend.user.model.UserRoleEnum;
import com.sparta.backend.user.repository.RefreshTokenRedisRepository;
import com.sparta.backend.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final JwtUtil jwtUtil;


    // ADMIN_TOKEN
    private final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";

    public ResponseEntity<String> signup(SignupRequestDto requestDto) {
        String username = requestDto.getUsername();
        String password = passwordEncoder.encode(requestDto.getPassword());

        // 회원 중복 확인
        Optional<User> checkUsername = userRepository.findByUsername(username);
        if (checkUsername.isPresent()) {
            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
        }

        // email 중복확인
        String email = requestDto.getEmail();
        Optional<User> checkEmail = userRepository.findByEmail(email);
        if (checkEmail.isPresent()) {
            throw new IllegalArgumentException("중복된 Email 입니다.");
        }

        // 사용자 ROLE 확인
        UserRoleEnum role = UserRoleEnum.USER;

        // 사용자 등록
        User user = new User(username, password, email, role);
        userRepository.save(user);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    public List<UserResponseDto> getUsersByEmailPrefix(String prefix) {
        final int LIST_COUNT = 10;

        List<UserResponseDto> users = userRepository.findAllByEmailStartingWith(prefix).stream().map(UserResponseDto::new).toList();

        users = users.stream()
                .sorted(Comparator.comparing(UserResponseDto::getEmail))
                .limit(LIST_COUNT)
                .collect(Collectors.toList());


        return users;
    }

    public void refresh(HttpServletRequest request , HttpServletResponse response) {
        String accessToken = jwtUtil.getJwtFromHeader(request);
        Claims info = jwtUtil.getUserInfoFromToken(accessToken);
        String email = info.getSubject();
        log.info("email = {}", email);

        //redis에 토큰이 없을 때
        if (!refreshTokenRedisRepository.existsByKey(email)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            log.info("refreshTokenRedisRepository.existsByKey not found");
            return;
        }

        String refreshToken = refreshTokenRedisRepository.findByKey(email);

        //redis에 있는 토큰이 오류가 있을 때
        if(!jwtUtil.validateToken(refreshToken)){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            log.info("refreshTokenRedisRepository.existsByKey not validate");
            return;
        }

        //redis에 토큰이 있고 검증이 끝났다면
        String newToken = jwtUtil.createAccessToken(email, UserRoleEnum.USER);
        log.info("refreshTokenRedisRepository.existsByKey");

        // JSON 객체 생성
        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("email", email);
        userInfo.put("accessToken", newToken);

        // 응답 본문에 JSON 작성
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter out = response.getWriter()) {
            out.print(new Gson().toJson(userInfo)); // Gson 라이브러리를 사용하여 JSON 변환
            response.setStatus(HttpServletResponse.SC_OK);
            out.flush();
            log.info("flush");
        } catch (IOException e) {
            log.error("Failed to send response", e);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}