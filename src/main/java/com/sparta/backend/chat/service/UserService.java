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
        List<UserResponseDto> userResponseDto = new ArrayList<>();

        List<User> users = userRepository.findAll();

        for (User user : users) {
            userResponseDto.add(new UserResponseDto(user));
        }

        return userResponseDto;
    }

    // user 등록
    public UserResponseDto createUser(UserRequestDto requestDto) {

        User user = new User(requestDto);

        User saveUser = userRepository.save(user);

        UserResponseDto userResponseDto = new UserResponseDto(saveUser);

        return userResponseDto;
    }

}
