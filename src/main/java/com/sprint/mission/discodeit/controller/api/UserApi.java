package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Tag(name = "User", description = "사용자 계정 및 접속 상태 관리 API")
public interface UserApi {

    @Operation(summary = "회원가입 (사용자 생성)", description = "사용자 정보(JSON)와 선택적 프로필 이미지로 새 계정을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "사용자 생성 성공"),
            @ApiResponse(responseCode = "400", description = "이메일·사용자 이름 중복 또는 필수 파트(userCreateRequest) 누락")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<User> createUser(
            @Parameter(description = "사용자 생성 요청 JSON", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            @RequestPart("userCreateRequest") UserCreateRequest request,
            @Parameter(description = "프로필 이미지 파일 (선택)")
            @RequestPart(value = "profile", required = false) MultipartFile profile);

    @Operation(summary = "전체 사용자 목록 조회", description = "가입된 모든 사용자 목록과 온라인 여부를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "사용자 목록 조회 성공")
    @GetMapping
    ResponseEntity<List<UserDto>> findAll();

    @Operation(summary = "사용자 정보 수정", description = "사용자의 이름, 이메일, 비밀번호, 프로필 이미지를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자 정보 수정 성공"),
            @ApiResponse(responseCode = "400", description = "변경하려는 이메일 또는 사용자 이름이 이미 사용 중임"),
            @ApiResponse(responseCode = "404", description = "해당 ID의 사용자를 찾을 수 없음")
    })
    @PatchMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<User> updateUser(
            @Parameter(description = "수정할 사용자 UUID") @PathVariable UUID userId,
            @Parameter(description = "사용자 수정 요청 JSON", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            @RequestPart("userUpdateRequest") UserUpdateRequest request,
            @Parameter(description = "새 프로필 이미지 파일 (선택)")
            @RequestPart(value = "profile", required = false) MultipartFile profile);

    @Operation(summary = "회원 탈퇴 (사용자 삭제)", description = "사용자 계정 및 연관된 프로필, 읽음 상태, 접속 상태를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "사용자 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "삭제할 사용자를 찾을 수 없음")
    })
    @DeleteMapping("/{userId}")
    ResponseEntity<Void> deleteUser(
            @Parameter(description = "삭제할 사용자 UUID") @PathVariable UUID userId);

    @Operation(summary = "사용자 접속 상태 갱신 (UserId 기준)", description = "사용자 ID를 통해 마지막 활동 시각(lastActiveAt)을 갱신합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "접속 상태 갱신 성공"),
            @ApiResponse(responseCode = "404", description = "해당 사용자의 상태 정보를 찾을 수 없음")
    })
    @PatchMapping("/{userId}/userStatus")
    ResponseEntity<UserStatus> updateUserStatusByUserId(
            @Parameter(description = "대상 사용자 UUID") @PathVariable UUID userId,
            @RequestBody UserStatusUpdateRequest request);
}