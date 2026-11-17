package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusListResponse;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/readStatuses")
@RequiredArgsConstructor
@Tag(
        name = "ReadStatus",
        description = "메시지 읽음 상태 생성, 수정 및 조회 API"
)
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    @PostMapping
    @Operation(
            summary = "ReadStatus 생성",
            description = "User ID와 Channel ID를 이용하여 메시지 읽음 상태 정보를 생성합니다. 마지막 읽음 시각은 서버의 생성 시각으로 초기화됩니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "ReadStatus가 성공적으로 생성되었습니다.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = ReadStatusResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "요청값이 올바르지 않거나 해당 User와 Channel에 대한 ReadStatus가 이미 존재합니다.",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(type = "string"),
                            examples = @ExampleObject(
                                    value = "해당 사용자와 채널에 대한 메시지 수신 정보가 이미 존재합니다. 사용자 ID: {userId}, 채널 ID: {channelId}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "ReadStatus를 생성할 User 또는 Channel이 존재하지 않습니다.",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(type = "string")
                    )
            )
    })
    public ResponseEntity<ReadStatusResponse> createReadStatus(
            @Valid @RequestBody ReadStatusCreateRequest request
    ) {
        ReadStatusResponse readStatusResponse =
                this.readStatusService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(readStatusResponse);
    }

    @PatchMapping("/{readStatusId}")
    @Operation(
            summary = "ReadStatus 수정",
            description = "지정한 ReadStatus의 마지막 읽음 시각을 요청값으로 갱신하고 수정 시각을 업데이트합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "ReadStatus가 성공적으로 수정되었습니다.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = ReadStatusResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "수정할 ReadStatus가 존재하지 않습니다.",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(type = "string")
                    )
            )
    })
    public ResponseEntity<ReadStatusResponse> updateReadStatus(
            @PathVariable("readStatusId") UUID readStatusId,
            @Valid @RequestBody ReadStatusUpdateRequest request
    ) {
        ReadStatusResponse readStatusResponse =
                this.readStatusService.update(readStatusId, request);

        return ResponseEntity.ok(readStatusResponse);
    }

    @GetMapping
    @Operation(
            summary = "User별 ReadStatus 목록 조회",
            description = "지정한 User의 모든 메시지 읽음 상태 정보를 조회하고 JSON 배열로 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "ReadStatus 목록 조회에 성공했습니다.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(
                                    schema = @Schema(
                                            implementation = ReadStatusResponse.class
                                    )
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "조회할 User가 존재하지 않습니다.",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(type = "string")
                    )
            )
    })
    public ResponseEntity<List<ReadStatusResponse>> findReadStatusesByUserId(
            @RequestParam("userId") UUID userId
    ) {
        ReadStatusListResponse readStatusListResponse =
                this.readStatusService.findAllByUserId(userId);

        return ResponseEntity.ok(readStatusListResponse.readStatuses());
    }
}

