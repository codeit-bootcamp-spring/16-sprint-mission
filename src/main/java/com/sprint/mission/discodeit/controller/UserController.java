package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.users.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.users.UserUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;

import jakarta.validation.Valid; // DTO의 유효성 검사를 실행하도록 지정
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping({"/api/users"})
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /* 전체 사용자 조회(GET) */
    // 기존 코드는 사용자 목록 데이터만 반환하며, HTTP 응답은 Spring이 기본 처리한 반면,
    // 변경 코드는 ResponseEntity를 사용하여 HTTP 상태 코드와 헤더, 응답 데이터를 직접 제어한다.
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.readAllUser());
    }

    /* 사용자 등록(POST) */
    // 생성 성공 시 201 Created 반환
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<UserDto> postUser(
            @Valid @RequestPart("userCreateRequest") UserCreateRequest request,
            @RequestPart(value = "profile", required = false) MultipartFile profile) {

        UserDto user = userService.createUser(request, profile);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    /* 사용자 정보 수정(PATCH) */
    // 수정할 때 본인을 제외한 다른 유저의 이름이나 이메일을 수정할 경우 "409" 가 발생한다.
    @RequestMapping(
            value = "/{userId}",
            method = RequestMethod.PATCH,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public UserDto patchUser(
            @PathVariable UUID userId,
            @Valid @RequestPart("userUpdateRequest") UserUpdateRequest request,
            @RequestPart(value = "profile", required = false) MultipartFile profile) {

        return userService.updateUser(userId, request, profile);
    }

    /* 사용자 삭제(DELETE) */
    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}