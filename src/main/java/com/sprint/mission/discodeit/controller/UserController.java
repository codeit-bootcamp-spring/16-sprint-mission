package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentRequest;
import com.sprint.mission.discodeit.dto.request.CreateUserRequest;
import com.sprint.mission.discodeit.dto.request.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.request.UpdateUserStatusRequest;
import com.sprint.mission.discodeit.dto.response.FindUserResponse;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.dto.response.UserStatusResponse;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import jakarta.servlet.http.HttpSession;
import com.sprint.mission.discodeit.dto.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Tag(name = "User", description = "사용자 API")
@RestController
@AllArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;

    @Operation(summary = "사용자 생성", description = "사용자를 생성합니다. 프로필 이미지는 선택입니다.")
    // JSON 파트의 Content-Type을 명시해야 Swagger UI에서 보낸 요청이 415로 거절되지 않는다
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            encoding = @Encoding(name = "userCreateRequest", contentType = MediaType.APPLICATION_JSON_VALUE)))
    @ApiResponse(responseCode = "201", description = "생성 성공")
    @ApiResponse(responseCode = "400", description = "유저명 또는 이메일 중복",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponse> postUser(@RequestPart("userCreateRequest") CreateUserRequest request,
                                                 @RequestPart(value = "profile", required = false) MultipartFile profile) throws IOException {

        BinaryContentRequest contentRequest = null;
        if (profile != null) {
            contentRequest = new BinaryContentRequest(profile.getOriginalFilename(), profile.getSize(),
                    profile.getContentType(), profile.getBytes());
        }

        UserResponse response = userService.create(contentRequest, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "사용자 전체 조회", description = "모든 사용자를 조회합니다. 비밀번호는 포함되지 않습니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public ResponseEntity<List<FindUserResponse>> getUsers() {
        List<FindUserResponse> response = userService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "사용자 수정", description = "사용자 정보를 수정합니다. 프로필 이미지를 보내면 교체됩니다.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            encoding = @Encoding(name = "userUpdateRequest", contentType = MediaType.APPLICATION_JSON_VALUE)))
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @ApiResponse(responseCode = "400", description = "유저명 또는 이메일 중복",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "해당 사용자가 존재하지 않음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PatchMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponse> updateUser(@RequestPart("userUpdateRequest") UpdateUserRequest request,
                                                           @RequestPart(value = "profile", required = false) MultipartFile profile,
                                                           @PathVariable UUID userId) throws IOException {
        BinaryContentRequest contentRequest = null;
        if (profile != null) {
            contentRequest = new BinaryContentRequest(profile.getOriginalFilename(), profile.getSize(),
                    profile.getContentType(), profile.getBytes());
        }
        UserResponse response = userService.update(userId, contentRequest, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "사용자 접속 상태 수정", description = "사용자의 마지막 활동 시각을 갱신합니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @ApiResponse(responseCode = "400", description = "마지막 활동 시각 값이 올바르지 않음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "해당 사용자 또는 접속 상태가 존재하지 않음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PatchMapping("/{userId}/userStatus")
    public ResponseEntity<UserStatusResponse> updateUserOnlineStatus(@RequestBody UpdateUserStatusRequest request,
                                                               @PathVariable UUID userId) {
        UserStatusResponse response = userStatusService.updateByUserId(userId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "사용자 삭제", description = "사용자를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "삭제 성공", content = @Content)
    @ApiResponse(responseCode = "404", description = "해당 사용자가 존재하지 않음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @DeleteMapping("/{userId}")
    public ResponseEntity deleteUser(@PathVariable UUID userId) {
        userService.delete(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
