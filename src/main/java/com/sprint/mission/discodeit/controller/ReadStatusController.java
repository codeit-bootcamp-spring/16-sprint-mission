package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.CustomErrorResponse;
import com.sprint.mission.discodeit.service.ReadStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/readStatuses")
@Tag(name = "읽음 상태", description = "채널별 읽음 상태 관리 API")
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    public ReadStatusController(ReadStatusService readStatusService) {
        this.readStatusService = readStatusService;
    }

    @Operation(summary = "읽음 상태 등록", description = "사용자와 채널별로 마지막으로 읽은 시각을 등록하고 읽음 상태 반환")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "등록한 읽음 상태 반환",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ReadStatus.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": "b04f4b05-7513-4b24-a25a-a62c4dda5c7a",
                                      "createdAt": "2026-11-16T05:30:00Z",
                                      "updatedAt": "2026-11-16T05:30:00Z",
                                      "userId": "2141551d-6dd9-4deb-881a-e65be0472bcd",
                                      "channelId": "6dd80dd7-c1b2-4690-ac2a-9f58aefda3a2",
                                      "lastReadAt": "2026-11-16T05:29:00Z"
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "같은 사용자와 채널의 읽음 상태가 이미 등록됨",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(value = "{\"code\":\"400\",\"message\":\"이미 등록된 메시지 수신 정보\"}"))),
            @ApiResponse(responseCode = "404", description = "사용자나 채널을 찾을 수 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomErrorResponse.class)))
    })
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public ReadStatus createReadStatus(@RequestBody ReadStatusCreateRequest request){
        return readStatusService.create(request);
    }

    @Operation(summary = "읽음 상태 수정", description = "읽음 상태 ID로 기록을 찾아 마지막으로 읽은 시각을 갱신하고 읽음 상태 반환")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정한 읽음 상태 반환"),
            @ApiResponse(responseCode = "404", description = "읽음 상태를 찾을 수 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomErrorResponse.class)))
    })
    @RequestMapping(method = RequestMethod.PATCH,value = "/{readStatusId}")
    public ReadStatus updateReadStatus(@PathVariable UUID readStatusId,@RequestBody ReadStatusUpdateRequest request){
        ReadStatusUpdateRequest readStatusUpdateRequest = new ReadStatusUpdateRequest(readStatusId,request.newLastReadAt());
        return readStatusService.update(readStatusUpdateRequest);
    }

    @Operation(summary = "읽음 상태 목록 조회", description = "사용자 ID로 해당 사용자의 채널별 읽음 상태 목록 반환")
    @ApiResponse(responseCode = "200", description = "읽음 상태 목록을 반환하고 기록이 없으면 빈 배열 반환")
    @RequestMapping(method = RequestMethod.GET)
    public List<ReadStatus> findAllReadStatuses(@RequestParam("userId")UUID userId){
        return readStatusService.findAllByUserId(userId);
    }
}
