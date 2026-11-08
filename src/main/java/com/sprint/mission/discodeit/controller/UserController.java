package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.users.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.users.UserUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 사용자 등록(POST)
    @RequestMapping(method = RequestMethod.POST)
    public UserDto postUser(@RequestBody UserCreateRequest request) {
        return userService.createUser(request);
    }

    // 모든 사용자 조회(GET)
    // 기존 코드는 사용자 목록 데이터만 반환하며, HTTP 응답은 Spring이 기본 처리한 반면,
    // 변경 코드는 ResponseEntity를 사용하여 HTTP 상태 코드와 헤더, 응답 데이터를 직접 제어한다.
    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.readAllUser());
    }

    // 사용자 정보 수정(PUT)
    @RequestMapping(method = RequestMethod.PUT)
    public UserDto putUser(@RequestBody UserUpdateRequest request) {
        return userService.updateUser(request);
    }

    // 사용자 삭제(DELETE)
    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    public void deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);
    }
}