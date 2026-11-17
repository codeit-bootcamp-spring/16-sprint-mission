package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.request.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.user.UserDto;
import com.sprint.mission.discodeit.dto.response.userstatus.UserStatusDto;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    // 사용자 등록 (프로필 이미지 선택 첨부)
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<UserDto> create(
            @RequestPart("request") UserCreateRequest request,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        UserDto userDto = userService.create(request, profile);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userDto);
    }

    // 모든 사용자 조회
    @RequestMapping(
            value = "/findAll",
            method = RequestMethod.GET
    )
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    // 사용자 정보 수정
    @RequestMapping(
            value = "/{userId}",
            method = RequestMethod.PATCH
    )
    public ResponseEntity<UserDto> update(
            @PathVariable UUID userId,
            @RequestBody UserUpdateRequest request
    ) {
        UserUpdateRequest updateRequest = new UserUpdateRequest(
                userId,
                request.username(),
                request.email(),
                request.password(),
                request.profileImageName(),
                request.profileImageContentType(),
                request.profileImageSize(),
                request.profileImageData()
        );

        return ResponseEntity.ok(userService.update(updateRequest));
    }

    // 사용자 삭제
    @RequestMapping(
            value = "/{userId}",
            method = RequestMethod.DELETE
    )
    public ResponseEntity<Void> delete(
            @PathVariable UUID userId
    ) {
        userService.deleteById(userId);

        return ResponseEntity.noContent().build();
    }

    // 사용자 온라인 상태 업데이트
    @RequestMapping(
            value = "/{userId}/status",
            method = RequestMethod.PATCH
    )
    public ResponseEntity<UserStatusDto> updateStatus(
            @PathVariable UUID userId,
            @RequestBody UserStatusUpdateRequest request
    ) {
        UserStatusDto statusDto = userStatusService.updateByUserId(
                userId,
                new UserStatusUpdateRequest(
                        null,
                        request.lastActiveAt()
                )
        );

        return ResponseEntity.ok(statusDto);
    }
}