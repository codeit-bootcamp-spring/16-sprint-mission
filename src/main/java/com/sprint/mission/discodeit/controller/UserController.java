package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.users.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.users.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.users.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;

import jakarta.validation.Valid; // DTO의 유효성 검사를 실행하도록 지정
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.tags.Tag; // API 그룹 이름과 설명
import io.swagger.v3.oas.annotations.Operation; // 개별 API의 기능 설명
import io.swagger.v3.oas.annotations.Parameter; // PathVariable 등의 파라미터 설명
import io.swagger.v3.oas.annotations.responses.ApiResponse; // 개별 HTTP 응답 코드 설명
import io.swagger.v3.oas.annotations.media.Schema;

import com.sprint.mission.discodeit.annotation.swagger.user.UserCreateDocs;
import com.sprint.mission.discodeit.annotation.swagger.user.UserDeleteDocs;
import com.sprint.mission.discodeit.annotation.swagger.user.UserStatusUpdateDocs;
import com.sprint.mission.discodeit.annotation.swagger.user.UserUpdateDocs;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping({"/api/users"})
@RequiredArgsConstructor
@Tag(name = "User", description = "User API")
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    @Operation(summary = "전체 User 목록 조회")
    @ApiResponse(
            responseCode = "200", description = "User 목록 조회 성공"
    )
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity.ok(userService.readAllUser());
    }

    @UserCreateDocs
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<UserDto> create(
            @Valid @RequestPart("userCreateRequest") UserCreateRequest request,
            @Parameter(
                    description = "User 프로필 이미지",
                    schema = @Schema(type = "string", format = "binary")
            )
            @RequestPart(value = "profile", required = false) MultipartFile profile) {

        UserDto user = userService.createUser(request, profile);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @UserUpdateDocs
    @RequestMapping(
            value = "/{userId}",
            method = RequestMethod.PATCH,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public UserDto update(
            @Parameter(description = "수정할 User ID", required = true)
            @PathVariable UUID userId,
            @Valid @RequestPart("userUpdateRequest") UserUpdateRequest request,
            @Parameter(description = "수정할 User 프로필 이미지")
            @RequestPart(value = "profile", required = false) MultipartFile profile) {

        return userService.updateUser(userId, request, profile);
    }

    @UserDeleteDocs
    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(
            @Parameter(description = "삭제할 User ID", required = true)
            @PathVariable UUID userId) {

        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @UserStatusUpdateDocs
    @RequestMapping(value = "/{userId}/userStatus", method = RequestMethod.PATCH)
    public UserStatus updateUserStatusByUserId(
            @Parameter(
                    description = "상태를 변경할 User ID", required = true
            )
            @PathVariable UUID userId,
            @Valid @RequestBody UserStatusUpdateRequest request) {

        return userStatusService.updateByUserId(
                userId,
                request.newLastActiveAt());
    }
}