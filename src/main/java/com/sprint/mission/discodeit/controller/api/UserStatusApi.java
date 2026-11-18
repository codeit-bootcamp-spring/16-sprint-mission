package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "UserStatus", description = "사용자 온라인/오프라인 상태 상세 관리 API")
public interface UserStatusApi {

    @Operation(summary = "사용자 상태 생성", description = "특정 사용자의 접속 상태 레코드를 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "사용자 상태 생성 성공"),
            @ApiResponse(responseCode = "400", description = "해당 사용자의 상태 정보가 이미 존재함"),
            @ApiResponse(responseCode = "404", description = "대상 사용자 ID를 찾을 수 없음")
    })
    @PostMapping
    ResponseEntity<UserStatus> createUserStatus(@RequestBody UserStatusCreateRequest request);

    @Operation(summary = "단건 사용자 상태 조회", description = "상태 ID(userStatusId)로 접속 상태를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 ID의 사용자 상태를 찾을 수 없음")
    })
    @GetMapping("/{userStatusId}")
    ResponseEntity<UserStatus> getUserStatus(
            @Parameter(description = "조회할 사용자 상태 UUID") @PathVariable UUID userStatusId);

    @Operation(summary = "전체 사용자 상태 목록 조회", description = "모든 사용자의 접속 상태 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "목록 조회 성공")
    @GetMapping
    ResponseEntity<List<UserStatus>> getAllUserStatuses();

    @Operation(summary = "사용자 상태 수정 (StatusId 기준)", description = "상태 ID(userStatusId)로 마지막 활동 시각을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "404", description = "해당 ID의 사용자 상태를 찾을 수 없음")
    })
    @PutMapping("/{userStatusId}")
    ResponseEntity<UserStatus> updateUserStatus(
            @Parameter(description = "수정할 사용자 상태 UUID") @PathVariable UUID userStatusId,
            @RequestBody UserStatusUpdateRequest request);

    @Operation(summary = "사용자 상태 수정 (UserId 기준)", description = "사용자 ID(userId)로 마지막 활동 시각을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "404", description = "해당 사용자의 상태 정보를 찾을 수 없음")
    })
    @PutMapping("/user/{userId}")
    ResponseEntity<UserStatus> updateUserStatusByUserId(
            @Parameter(description = "대상 사용자 UUID") @PathVariable UUID userId,
            @RequestBody UserStatusUpdateRequest request);

    @Operation(summary = "사용자 상태 삭제", description = "상태 ID(userStatusId)로 접속 상태 정보를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "삭제할 사용자 상태를 찾을 수 없음")
    })
    @DeleteMapping("/{userStatusId}")
    ResponseEntity<Void> deleteUserStatus(
            @Parameter(description = "삭제할 사용자 상태 UUID") @PathVariable UUID userStatusId);
}