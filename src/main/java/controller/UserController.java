package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    // 1. 사용자 등록 (기존 기능)
    @RequestMapping(
            value = "/api/users",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public Object create(
            @RequestPart("userCreateRequest") UserCreateRequest request,
            @RequestPart(value = "profile", required = false)
            MultipartFile profile
    ) throws IOException {

        return userService.create(
                request,
                toProfileRequest(profile)
        );
    }

    // 2. 기존 사용자 전체 조회
    @RequestMapping(
            value = "/api/users",
            method = RequestMethod.GET
    )
    public Object findAll() {
        return userService.findAll();
    }

    // 3. 심화 요구사항: 사용자 전체 조회
    @RequestMapping(
            value = "/api/user/findAll",
            method = RequestMethod.GET
    )
    public ResponseEntity<List<UserDto>> findAllAdvanced() {
        return ResponseEntity.ok(userService.findAll());
    }

    // 4. 사용자 정보 수정
    @RequestMapping(
            value = "/api/users/{userId}",
            method = RequestMethod.PATCH,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public Object update(
            @PathVariable UUID userId,
            @RequestPart("userUpdateRequest") UserUpdateRequest request,
            @RequestPart(value = "profile", required = false)
            MultipartFile profile
    ) throws IOException {

        return userService.update(
                userId,
                request,
                toProfileRequest(profile)
        );
    }

    // 5. 사용자 삭제
    @RequestMapping(
            value = "/api/users/{userId}",
            method = RequestMethod.DELETE
    )
    public void delete(@PathVariable UUID userId) {
        userService.delete(userId);
    }

    // 6. 사용자 온라인 상태 업데이트
    @RequestMapping(
            value = "/api/users/{userId}/status",
            method = RequestMethod.PATCH
    )
    public Object updateStatus(
            @PathVariable UUID userId,
            @RequestBody UserStatusUpdateRequest request
    ) {
        return userStatusService.updateByUserId(userId, request);
    }

    // 프로필 이미지를 서비스 요청 객체로 변환
    private Optional<BinaryContentCreateRequest> toProfileRequest(
            MultipartFile profile
    ) throws IOException {

        if (profile == null || profile.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(
                new BinaryContentCreateRequest(
                        profile.getOriginalFilename(),
                        profile.getContentType(),
                        profile.getBytes()
                )
        );
    }
}
