package com.sparta.backend.chat.service;

import com.sparta.backend.chat.dto.user.UserRequestDto;
import com.sparta.backend.chat.dto.user.UserResponseDto;
import com.sparta.backend.chat.entity.User;
import com.sparta.backend.chat.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // user 조회
    public List<UserResponseDto> getUsers() {

        // 사용자 정보 List 생성
        List<UserResponseDto> userResponseDto = new ArrayList<>();

        // 모든 사용자 조회
        List<User> users = userRepository.findAll();

        //사용자 객체를 dto로 변환하여 List에 추가
        for (User user : users) {
            userResponseDto.add(new UserResponseDto(user));
        }

        return userResponseDto;
    }

    // user 등록
    public UserResponseDto createUser(UserRequestDto requestDto) {

        // User 엔티티 생성
        User user = new User(requestDto);

        // User 저장
        User saveUser = userRepository.save(user);

        // User 객체를 dto로 변환
        UserResponseDto userResponseDto = new UserResponseDto(saveUser);

        return userResponseDto;
    }
}
