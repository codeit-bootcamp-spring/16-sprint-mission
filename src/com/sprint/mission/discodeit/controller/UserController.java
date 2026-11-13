package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.request.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.user.UserDto;
import com.sprint.mission.discodeit.dto.response.userstatus.UserStatusDto;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

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
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    // 사용자 등록
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "사용자 생성 성공"),
            @ApiResponse(
                    responseCode = "400",
                    description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
                    content = @Content(
                            schema = @Schema(implementation = Map.class)
                    )
            )
    })
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<UserDto> create(
            @RequestPart("userCreateRequest") UserCreateRequest request,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        UserDto userDto = userService.create(request, profile);

        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }

    // 모든 사용자 조회
    @ApiResponse(responseCode = "200", description = "사용자 목록 조회 성공")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    // 사용자 정보 수정
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자 정보 수정 성공"),
            @ApiResponse(
                    responseCode = "400",
                    description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
                    content = @Content(
                            schema = @Schema(implementation = Map.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User를 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = Map.class)
                    )
            )
    })
    @RequestMapping(
            value = "/{userId}",
            method = RequestMethod.PATCH,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<UserDto> update(
            @PathVariable UUID userId,
            @RequestPart("userUpdateRequest") UserUpdateRequest request,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        return ResponseEntity.ok(userService.update(userId, request, profile));
    }

    // 사용자 삭제
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "사용자 삭제 성공"),
            @ApiResponse(
                    responseCode = "404",
                    description = "User를 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = Map.class)
                    )
            )
    })
    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable UUID userId) {
        userService.deleteById(userId);

        return ResponseEntity.noContent().build();
    }

    // 사용자 온라인 상태 업데이트
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자 상태 수정 성공"),
            @ApiResponse(
                    responseCode = "404",
                    description = "해당 User의 UserStatus를 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = Map.class)
                    )
            )
    })
    @RequestMapping(
            value = "/{userId}/userStatus",
            method = RequestMethod.PATCH,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserStatusDto> updateStatus(
            @PathVariable UUID userId,
            @RequestBody UserStatusUpdateRequest request
    ) {
        UserStatusDto statusDto = userStatusService.updateByUserId(userId, request);

        return ResponseEntity.ok(statusDto);
    }
}