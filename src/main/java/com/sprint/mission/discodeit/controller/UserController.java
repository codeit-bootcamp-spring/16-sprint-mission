package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.*;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponse;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(
        name = "User",
        description = "사용자 등록, 조회, 수정, 삭제 및 온라인 상태 관리 API"
)
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "User 등록",
            description = "사용자명, 이메일, 비밀번호와 선택적인 프로필 이미지를 이용하여 User를 생성합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "User가 성공적으로 생성되었습니다.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "사용자명 또는 이메일 중복, 유효성 검증 실패, 잘못된 요청 형식",
                    content = {
                            @Content(
                                    mediaType = MediaType.TEXT_PLAIN_VALUE,
                                    schema = @Schema(type = "string"),
                                    examples = {
                                            @ExampleObject(
                                                    name = "이메일 중복",
                                                    value = "이미 사용 중인 이메일입니다: user@example.com"
                                            ),
                                            @ExampleObject(
                                                    name = "사용자명 중복",
                                                    value = "이미 사용 중인 사용자 이름입니다: username"
                                            ),
                                            @ExampleObject(
                                                    name = "유효성 검증 실패",
                                                    value = "요청값이 올바르지 않습니다."
                                            )
                                    }
                            ),
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(type = "object")
                            )
                    }
            )
    })
    public ResponseEntity<UserDto> createUser(
            @Valid @RequestPart("userCreateRequest") UserCreateRequest request,
            @RequestPart(value = "profile", required = false)
            MultipartFile profile
    ) throws IOException {
        UserDto userDto = userService.create(request, profile);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userDto);
    }

    @PatchMapping(value = "/{userId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(
            summary = "User 수정",
            description = "지정한 User의 사용자명, 이메일, 비밀번호 또는 프로필 이미지를 수정합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User가 성공적으로 수정되었습니다.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "사용자명 또는 이메일 중복, 유효성 검증 실패, 잘못된 UUID 또는 요청 형식",
                    content = {
                            @Content(
                                    mediaType = MediaType.TEXT_PLAIN_VALUE,
                                    schema = @Schema(type = "string")
                            ),
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(type = "object")
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "수정할 User가 존재하지 않습니다.",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(type = "string")
                    )
            )
    })
    public ResponseEntity<UserDto> updateUser(
            @PathVariable("userId") UUID userId,
            @Valid @RequestPart("userUpdateRequest") UserUpdateRequest request,
            @RequestPart(value = "profile", required = false)
            MultipartFile profile) throws IOException {
        UserDto userDto = this.userService.update(
                userId, request, profile
        );

        return ResponseEntity.ok(userDto);
    }

    @DeleteMapping("/{userId}")
    @Operation(
            summary = "User 삭제",
            description = "지정한 User와 관련된 사용자 상태, 읽음 상태, 메시지 및 파일 데이터를 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "User가 성공적으로 삭제되었습니다. 응답 본문은 없습니다.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "userId의 UUID 형식이 올바르지 않습니다.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(type = "object")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "삭제할 User 또는 관련 사용자 상태가 존재하지 않습니다.",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(type = "string")
                    )
            )
    })
    public ResponseEntity<Void> deleteUser(
            @PathVariable("userId") UUID userId
    ) {
        this.userService.delete(userId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(
            summary = "User 목록 조회",
            description = "등록된 모든 User의 정보를 조회하고 JSON 배열로 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User 목록 조회에 성공했습니다.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(
                                    schema = @Schema(
                                            implementation = UserResponse.class
                                    )
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "조회 대상 User에 연결된 사용자 상태 정보를 찾을 수 없습니다.",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(type = "string")
                    )
            )
    })
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        UserListResponse userListResponse = this.userService.findAll();


        return ResponseEntity.ok(userListResponse.users());
    }

    @PatchMapping("/{userId}/userStatus")
    @Operation(
            summary = "User 온라인 상태 갱신",
            description = "지정한 User의 마지막 활동 시각을 서버 현재 시각으로 갱신하고 온라인 상태를 업데이트합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User 온라인 상태가 성공적으로 갱신되었습니다.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = UserStatusResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 UUID 또는 요청 본문 형식",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(type = "object")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "해당 User 또는 UserStatus가 존재하지 않습니다.",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(type = "string")
                    )
            )
    })
    public ResponseEntity<UserStatusResponse> updateUserOnlineStatus(
            @PathVariable("userId") UUID userId,
            @Valid @RequestBody UserStatusUpdateRequest request
    ) {
        UserStatusResponse userStatusResponse =
                this.userStatusService.updateByUserId(userId);

        return ResponseEntity.ok(userStatusResponse);
    }
}
