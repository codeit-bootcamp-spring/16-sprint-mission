package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.service.ReadStatusService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "ReadStatus", description = "읽음 상태 생성, 조회 및 수정 API")
@RestController
@RequestMapping("/api/readStatuses")
@RequiredArgsConstructor
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    // 읽음 상태 생성
    @Operation(
            summary = "읽음 상태 생성",
            description = "사용자와 채널을 연결하는 새로운 읽음 상태를 생성합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "읽음 상태 생성 성공",
                    content = @Content(
                            schema = @Schema(implementation = ReadStatusDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 또는 이미 존재하는 읽음 상태",
                    content = @Content(
                            schema = @Schema(implementation = Map.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자 또는 채널을 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = Map.class)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<ReadStatusDto> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "읽음 상태 생성 정보 (사용자 ID, 채널 ID)",
                    required = true
            )
            @RequestBody ReadStatusCreateRequest request
    ) {
        ReadStatusDto readStatusDto = readStatusService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(readStatusDto);
    }

    // 사용자별 읽음 상태 목록 조회
    @Operation(
            summary = "사용자별 읽음 상태 목록 조회",
            description = "지정한 사용자가 참여하는 채널의 읽음 상태 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "읽음 상태 목록 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "사용자 ID 형식이 올바르지 않음",
                    content = @Content(
                            schema = @Schema(implementation = Map.class)
                    )
            )
    })
    @GetMapping
    public ResponseEntity<List<ReadStatusDto>> findAllByUserId(
            @Parameter(
                    description = "조회할 사용자 ID (UUID)",
                    required = true,
                    example = "baa027d6-0d46-41ca-b085-36aeef45dad6"
            )
            @RequestParam UUID userId
    ) {
        return ResponseEntity.ok(
                readStatusService.findAllByUserId(userId)
        );
    }

    // 읽음 상태 수정
    @Operation(
            summary = "읽음 상태 수정",
            description = "지정한 읽음 상태의 마지막으로 읽은 시각을 수정합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "읽음 상태 수정 성공",
                    content = @Content(
                            schema = @Schema(implementation = ReadStatusDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 데이터",
                    content = @Content(
                            schema = @Schema(implementation = Map.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 읽음 상태",
                    content = @Content(
                            schema = @Schema(implementation = Map.class)
                    )
            )
    })
    @PatchMapping("/{readStatusId}")
    public ResponseEntity<ReadStatusDto> update(
            @Parameter(
                    description = "수정할 읽음 상태 ID (UUID)",
                    required = true,
                    example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
            )
            @PathVariable UUID readStatusId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "수정할 읽음 상태 정보",
                    required = true
            )
            @RequestBody ReadStatusUpdateRequest request
    ) {
        return ResponseEntity.ok(
                readStatusService.update(readStatusId, request)
        );
    }
}