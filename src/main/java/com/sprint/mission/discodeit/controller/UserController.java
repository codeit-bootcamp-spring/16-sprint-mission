package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.CustomErrorResponse;
import com.sprint.mission.discodeit.exception.InvalidRequestException;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@Tag(name = "사용자", description = "사용자 정보와 접속 상태 관리 API")
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;

    public UserController(UserService userService, UserStatusService userStatusService) {
        this.userService = userService;
        this.userStatusService = userStatusService;
    }

    @Operation(summary = "사용자 목록 조회", description = "온라인 상태가 포함된 전체 사용자 목록 반환")
    @ApiResponse(responseCode = "200", description = "사용자 목록을 반환하고 사용자가 없으면 빈 배열 반환")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @Operation(summary = "사용자 등록", description = "사용자를 등록하고 프로필 이미지가 있으면 함께 저장한 뒤 등록된 사용자 정보 반환")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "등록한 사용자 정보 반환",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": "2141551d-6dd9-4deb-881a-e65be0472bcd",
                                      "createdAt": "2026-11-16T02:43:46Z",
                                      "updatedAt": "2026-11-16T02:43:46Z",
                                      "username": "sprint5-test",
                                      "email": "sprint5-test@example.com",
                                      "profileId": null
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "사용자 이름이나 이메일이 이미 사용 중인 경우",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(value = "{\"code\":\"400\",\"message\":\"사용 중인 이메일\"}")))
    })
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@RequestPart("userCreateRequest") UserCreateRequest request, @RequestPart(value = "profile", required = false)
    MultipartFile profile) {
        User user = userService.create(request, profile);
        return new UserResponse(user.getId(), user.getCreatedAt(), user.getUpdatedAt(), user.getName(), user.getEmail(), user.getProfileId());
    }

    @Operation(summary = "사용자 삭제", description = "사용자 ID로 사용자를 찾아 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공", content = @Content),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomErrorResponse.class)))
    })
    @RequestMapping(method = RequestMethod.DELETE, value = "/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID userId){
        userService.delete(userId);
    }

    @Operation(summary = "사용자 정보 수정", description = "사용자 ID로 사용자를 찾아 정보를 수정하고 수정된 사용자 정보 반환")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정한 사용자 정보 반환"),
            @ApiResponse(responseCode = "400", description = "변경할 이름이나 이메일이 이미 사용 중인 경우",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomErrorResponse.class)))
    })
    @RequestMapping(method = RequestMethod.PATCH,value = "/{userId}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UserResponse update(@PathVariable UUID userId,@RequestPart("userUpdateRequest") UserUpdateRequest request,
                               @RequestPart(value = "profile", required = false)MultipartFile profile) {
        User user = userService.update(new UserUpdateRequest(userId, request.newUsername(),
                request.newPassword(), request.newEmail()), profile);
        return new UserResponse(user.getId(), user.getCreatedAt(), user.getUpdatedAt(),
                user.getName(), user.getEmail(), user.getProfileId());
    }

    @Operation(summary = "사용자 접속 상태 갱신", description = "사용자 ID로 사용자를 찾아 마지막 활동 시각을 갱신하고 접속 상태 반환")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "갱신한 접속 상태 반환"),
            @ApiResponse(responseCode = "400", description = "마지막 활동 시각이 누락된 경우",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자나 접속 상태를 찾을 수 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomErrorResponse.class)))
    })
    @PatchMapping("/{userId}/userStatus")
    public UserStatus onlineStatus(@PathVariable UUID userId, @RequestBody UserActivityRequest request)
    {
        if (request.newLastActiveAt() == null) {
            throw new InvalidRequestException("newLastActiveAt은 필수입니다");
        }

        return userStatusService.updateByUserId(userId, request.newLastActiveAt());
    }
}
