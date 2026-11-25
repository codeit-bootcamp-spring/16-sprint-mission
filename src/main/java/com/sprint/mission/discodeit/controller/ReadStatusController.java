package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "ReadStatus", description = "채널별 읽음 상태 등록, 수정 및 조회")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/readStatuses")
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    @Operation(summary = "읽음 상태 등록")
    @ApiResponse(responseCode = "201", description = "읽음 상태 등록 성공")
    @ApiResponse(
            responseCode = "400",
            description = "같은 사용자와 채널의 읽음 상태가 이미 있거나 요청 JSON을 읽을 수 없음",
            content = @Content(
                    schema = @Schema(implementation = String.class),
                    examples = @ExampleObject(
                            name = "duplicateReadStatus",
                            value = "해당 사용자와 채널의 읽음 상태가 이미 존재합니다."
                    )
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "사용자 또는 채널을 찾을 수 없음",
            content = @Content(
                    schema = @Schema(implementation = String.class),
                    examples = {
                            @ExampleObject(
                                    name = "userNotFound",
                                    value = "User with id 88e75d27-b6b7-488a-a19e-51ded3868800 does not exist"
                            ),
                            @ExampleObject(
                                    name = "channelNotFound",
                                    value = "Channel with id 88e75d27-b6b7-488a-a19e-51ded3868800 does not exist"
                            )
                    }
            )
    )
    @ApiResponse(
            responseCode = "415",
            description = "요청 본문의 Content-Type을 지원하지 않음",
            content = @Content(
                    schema = @Schema(implementation = String.class),
                    examples = @ExampleObject(
                            name = "unsupportedContentType",
                            value = "Content-Type 'text/plain' is not supported"
                    )
            )
    )
    @PostMapping
    public ResponseEntity<ReadStatus> create(@RequestBody ReadStatusCreateRequest request) {
        ReadStatus createdReadStatus = readStatusService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReadStatus);
    }

    @Operation(summary = "마지막 읽음 시각 수정")
    @ApiResponse(responseCode = "200", description = "읽음 상태 수정 성공")
    @ApiResponse(
            responseCode = "400",
            description = "읽음 상태 ID의 UUID 형식이 잘못되었거나 요청 JSON을 읽을 수 없음",
            content = @Content(
                    schema = @Schema(implementation = String.class),
                    examples = @ExampleObject(
                            name = "invalidReadStatusId",
                            value = "Failed to convert value of type 'java.lang.String' to required type 'java.util.UUID'; Invalid UUID string: abc"
                    )
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "수정할 읽음 상태를 찾을 수 없음",
            content = @Content(
                    schema = @Schema(implementation = String.class),
                    examples = @ExampleObject(
                            name = "readStatusNotFound",
                            value = "ReadStatus with id 88e75d27-b6b7-488a-a19e-51ded3868800 not found"
                    )
            )
    )
    @ApiResponse(
            responseCode = "415",
            description = "요청 본문의 Content-Type을 지원하지 않음",
            content = @Content(
                    schema = @Schema(implementation = String.class),
                    examples = @ExampleObject(
                            name = "unsupportedContentType",
                            value = "Content-Type 'text/plain' is not supported"
                    )
            )
    )
    @PatchMapping("/{readStatusId}")
    public ResponseEntity<ReadStatus> update(
            @PathVariable("readStatusId") UUID readStatusId,
            @RequestBody ReadStatusUpdateRequest request
    ) {
        ReadStatus updatedReadStatus = readStatusService.update(readStatusId, request);
        return ResponseEntity.ok(updatedReadStatus);
    }

    @Operation(summary = "사용자별 읽음 상태 목록 조회")
    @ApiResponse(responseCode = "200", description = "읽음 상태 목록 조회 성공. 조회 결과가 없으면 빈 배열")
    @ApiResponse(
            responseCode = "400",
            description = "userId가 누락되었거나 UUID 형식이 올바르지 않음",
            content = @Content(
                    schema = @Schema(implementation = String.class),
                    examples = {
                            @ExampleObject(
                                    name = "missingUserId",
                                    value = "Required request parameter 'userId' for method parameter type UUID is not present"
                            ),
                            @ExampleObject(
                                    name = "invalidUserId",
                                    value = "Failed to convert value of type 'java.lang.String' to required type 'java.util.UUID'; Invalid UUID string: abc"
                            )
                    }
            )
    )
    @GetMapping
    public ResponseEntity<List<ReadStatus>> findAllByUserId(
            @RequestParam("userId") UUID userId
    ) {
        return ResponseEntity.ok(readStatusService.findAllByUserId(userId));
    }
}