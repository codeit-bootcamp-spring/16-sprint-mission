package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor

public class UserController {
    private final UserService userService;
    // 유저 생성 (회원가입)
    @PostMapping
    public UserDto createUser(@RequestBody UserCreateRequest request) {
        return userService.find(userService.create(request, Optional.empty()).getId());
    }

    // 단일 유저 조회
    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable UUID userId) {
        return userService.find(userId);
    }

    // 전체 유저 조회
    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.findAll();
    }

    // 유저 수정
    @PutMapping("/{userId}")
    public UserDto updateUser(@PathVariable UUID userId, @RequestBody UserUpdateRequest request) {
        return userService.find(userService.update(userId, request, Optional.empty()).getId());
    }

    // 유저 삭제
    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable UUID userId) {
        userService.delete(userId);
    }
}