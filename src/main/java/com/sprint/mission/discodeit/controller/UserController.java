package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.users.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.users.UserUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;

import jakarta.validation.Valid; // DTO의 유효성 검사를 실행하도록 지정
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 사용자 등록(POST)
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public UserDto postUser(
            @Valid @RequestPart("request") UserCreateRequest request,
            @RequestPart(value = "profile", required = false) MultipartFile profile) {

        return userService.createUser(request, profile);
    }

    // 사용자 단건 조회(GET)
    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public ResponseEntity<UserDto> getUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.readUser(userId));
    }

    // 모든 사용자 조회(GET)
    // 기존 코드는 사용자 목록 데이터만 반환하며, HTTP 응답은 Spring이 기본 처리한 반면,
    // 변경 코드는 ResponseEntity를 사용하여 HTTP 상태 코드와 헤더, 응답 데이터를 직접 제어한다.
    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.readAllUser());
    }

    // 사용자 정보 수정(PUT)
    // 수정할 때 본인을 제외한 다른 유저의 이름이나 이메일을 수정할 경우 "409" 가 발생한다.
    @RequestMapping(
            method = RequestMethod.PUT,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public UserDto putUser(
            @Valid @RequestPart("request") UserUpdateRequest request,
            @RequestPart(value = "profile", required = false) MultipartFile profile) {

        return userService.updateUser(request, profile);
    }

    // 사용자 삭제(DELETE)
    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    public void deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);
    }
}