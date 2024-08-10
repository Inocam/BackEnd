package com.sparta.backend.user.service;

import com.google.gson.Gson;
import com.sparta.backend.security.JwtUtil;
import com.sparta.backend.user.dto.LoginRequestDto;
import com.sparta.backend.user.dto.SignupRequestDto;
import com.sparta.backend.user.dto.UserResponseDto;
import com.sparta.backend.user.model.User;
import com.sparta.backend.user.model.UserRoleEnum;
import com.sparta.backend.user.repository.RefreshTokenRedisRepository;
import com.sparta.backend.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    // ADMIN_TOKEN
    private final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";

    public void signup(SignupRequestDto requestDto) {
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
    }

    public List<UserResponseDto> getUsersByUsernamePrefix(String prefix) {
        return userRepository.findByUsernameStartingWith(prefix).stream().map(UserResponseDto::new).toList();
    }

    public void loginTest(HttpServletResponse res, LoginRequestDto loginRequestDto) {
        String email = loginRequestDto.getEmail();
        String inputPassword = loginRequestDto.getPassword();
        String dbPassword = userRepository.findByEmail(email).get().getPassword();
        if(!passwordEncoder.encode(inputPassword).equals(dbPassword)) {
            return;
        }

        String accessToken = jwtUtil.createAccessToken(email, UserRoleEnum.USER);
        String refreshToken = jwtUtil.createRefreshToken(email);

        refreshTokenRedisRepository.save(email, refreshToken);

        // JSON 객체 생성
        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("accessToken", accessToken);
        userInfo.put("id", userRepository.findByEmail(email).get().getId().toString());
        userInfo.put("username", userRepository.findByEmail(email).get().getUsername().toString());
        userInfo.put("email", email);


        // 응답 본문에 JSON 작성
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        try (PrintWriter out = res.getWriter()) {
            out.print(new Gson().toJson(userInfo)); // Gson 라이브러리를 사용하여 JSON 변환
            out.flush();
        } catch (IOException e) {
            res.setStatus(401);
        }


    }
}