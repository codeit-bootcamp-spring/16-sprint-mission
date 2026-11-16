package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readStatus.*;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.service.basic.ReadStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "ReadStatus", description = "메세지 읽음 상태")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("api/readStatuses")
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    @Operation(summary = "사용자의 메세지 읽음 상태 조회")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "사용자의 메세지 읽음 상태 조회 성공"
            )
    })
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatusResponse>> findAllReadStatusByUserId(@Parameter(description = "사용자 ID") @NotNull @RequestParam UUID userId) {
        return ResponseEntity.status(HttpStatus.OK).body(this.readStatusService.findAllByUserId(userId));
    }

    @Operation(summary = "메세지 읽음 상태 생성")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "메세지 읽음 상태 생성 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "메세지 읽음 상태가 이미 존재함",
                    content = @Content(schema = @Schema(
                            implementation = ErrorResponse.class
                    ))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자 또는 채널이 존재하지 않음",
                    content = @Content(schema = @Schema(
                            implementation = ErrorResponse.class
                    ))
            )
    })
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ReadStatusResponse> createReadStatus(@Valid @RequestBody ReadStatusCreateRequest createRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.readStatusService.create(createRequest));
    }

    @Operation(summary = "메세지 읽음 상태 수정")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "메세지 읽음 상태 수정 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "삭제 메세지 읽음 상태를 찾을 수 없음",
                    content = @Content(schema = @Schema(
                            implementation = ErrorResponse.class
                    ))
            )
    })
    @RequestMapping(value = "/{readStatusId}", method = RequestMethod.PATCH)
    public ResponseEntity<ReadStatusResponse> updateReadStatus(@Parameter(description = "메세지 읽음 상태 ID") @NotNull @PathVariable UUID readStatusId,
                                                               @Valid @RequestBody ReadStatusUpdateRequest updateRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(this.readStatusService.update(readStatusId, updateRequest));
    }
}
