package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.service.ReadStatusService;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/readStatuses")
@RequiredArgsConstructor
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    // 읽음 상태 생성
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "읽음 상태 생성 성공"),
            @ApiResponse(
                    responseCode = "400",
                    description = "이미 존재하는 읽음 상태",
                    content = @Content(
                            schema = @Schema(implementation = Map.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User 또는 Channel을 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = Map.class)
                    )
            )
    })
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ReadStatusDto> create(
            @RequestBody ReadStatusCreateRequest request
    ) {
        ReadStatusDto readStatusDto = readStatusService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(readStatusDto);
    }

    // 특정 사용자의 읽음 상태 조회
    @ApiResponse(responseCode = "200", description = "읽음 상태 목록 조회 성공")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatusDto>> findAllByUserId(
            @RequestParam UUID userId
    ) {
        return ResponseEntity.ok(
                readStatusService.findAllByUserId(userId)
        );
    }

    // 읽음 상태 수정
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "읽음 상태 수정 성공"),
            @ApiResponse(
                    responseCode = "404",
                    description = "ReadStatus를 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = Map.class)
                    )
            )
    })
    @RequestMapping(
            value = "/{readStatusId}",
            method = RequestMethod.PATCH
    )
    public ResponseEntity<ReadStatusDto> update(
            @PathVariable UUID readStatusId,
            @RequestBody ReadStatusUpdateRequest request
    ) {
        return ResponseEntity.ok(
                readStatusService.update(readStatusId, request)
        );
    }
}