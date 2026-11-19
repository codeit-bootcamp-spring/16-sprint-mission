package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.CreateReadStatusRequest;
import com.sprint.mission.discodeit.dto.request.UpdateReadStatusRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusResponse;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sprint.mission.discodeit.dto.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "ReadStatus", description = "메시지 읽음 상태 API")
@RestController
@AllArgsConstructor
@RequestMapping("/api/readStatuses")
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    @Operation(summary = "읽음 상태 생성", description = "사용자가 채널을 마지막으로 읽은 시각을 기록합니다.")
    @ApiResponse(responseCode = "201", description = "생성 성공")
    @ApiResponse(responseCode = "400", description = "id가 유효하지 않거나, 같은 사용자·채널의 읽음 상태가 이미 존재함",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "해당 사용자 또는 채널이 존재하지 않음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping
    public ResponseEntity<ReadStatusResponse> createReadStatus(@RequestBody CreateReadStatusRequest request) {
        ReadStatusResponse response = readStatusService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "읽음 상태 수정", description = "마지막으로 읽은 시각을 갱신합니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @ApiResponse(responseCode = "400", description = "읽은 시각이 없거나 현재보다 미래임",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "해당 읽음 상태가 존재하지 않음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PatchMapping("/{readStatusId}")
    public ResponseEntity<ReadStatusResponse> updateReadStatus(@RequestBody UpdateReadStatusRequest request,
                                                               @PathVariable UUID readStatusId) {
        ReadStatusResponse response = readStatusService.update(readStatusId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "사용자의 읽음 상태 목록 조회", description = "해당 사용자의 모든 읽음 상태를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "404", description = "해당 사용자가 존재하지 않음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping
    public ResponseEntity<List<ReadStatusResponse>> getReadStatus(@RequestParam UUID userId) {
        List<ReadStatusResponse> response = readStatusService.findAllByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
