package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "ReadStatus", description = "사용자별 채널 메시지 읽음 상태 관리 API")
public interface ReadStatusApi {

    @Operation(summary = "읽음 상태 생성", description = "특정 사용자의 채널 읽음 상태 정보를 새로 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "읽음 상태 생성 성공"),
            @ApiResponse(responseCode = "400", description = "해당 사용자와 채널의 읽음 상태가 이미 존재함"),
            @ApiResponse(responseCode = "404", description = "사용자 또는 채널 ID를 찾을 수 없음")
    })
    @PostMapping
    ResponseEntity<ReadStatus> createReadStatus(@RequestBody ReadStatusCreateRequest request);

    @Operation(summary = "읽음 상태 갱신", description = "마지막으로 메시지를 읽은 시각(lastReadAt)을 업데이트합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "읽음 상태 수정 성공"),
            @ApiResponse(responseCode = "404", description = "해당 ID의 읽음 상태를 찾을 수 없음")
    })
    @PatchMapping("/{readStatusId}")
    ResponseEntity<ReadStatus> updateReadStatus(
            @Parameter(description = "수정할 읽음 상태 UUID") @PathVariable UUID readStatusId,
            @RequestBody ReadStatusUpdateRequest request);

    @Operation(summary = "사용자별 읽음 상태 목록 조회", description = "특정 사용자의 모든 채널 읽음 상태 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "읽음 상태 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "필수 파라미터(userId) 누락 또는 형식 오류")
    })
    @GetMapping
    ResponseEntity<List<ReadStatus>> findAllByUserId(
            @Parameter(description = "조회할 사용자 UUID") @RequestParam UUID userId);
}