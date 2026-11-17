package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequestDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequestDto;
import com.sprint.mission.discodeit.exception.ErrorResponse;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "ReadStatus", description = "Message 읽음 상태 API")
public interface ReadStatusApi {

    @Operation(summary = "Message 읽음 상태 생성")
    @ApiResponse(responseCode = "201", description = "Message 읽음 상태가 성공적으로 생성됨")
    @ApiResponse(
            responseCode = "400",
            description = "이미 읽음 상태가 존재함",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                            name = "읽음 상태 중복",
                            value = """
                                    {
                                      "status": 400,
                                      "message": "이미 해당 사용자의 채널 읽음 상태가 존재합니다."
                                    }
                                    """
                    )
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Channel 또는 User를 찾을 수 없음",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                            @ExampleObject(
                                    name = "채널 없음",
                                    value = """
                                            {
                                              "status": 404,
                                              "message": "채널을 찾을 수 없습니다. %s"
                                            }
                                            """
                            ),
                            @ExampleObject(
                                    name = "사용자 없음",
                                    value = """
                                            {
                                              "status": 404,
                                              "message": "사용자를 찾을 수 없습니다. %s"
                                            }
                                            """
                            )
                    }
            )
    )
    ReadStatusResponseDto create(
            @Valid @RequestBody ReadStatusCreateRequestDto dto
    );

    @Operation(summary = "Message 읽음 상태 수정")
    @ApiResponse(responseCode = "200", description = "Message 읽음 상태가 성공적으로 수정됨")
    @ApiResponse(
            responseCode = "404",
            description = "User를 찾을 수 없음",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                            name = "Message 읽음 상태를 찾을 수 없음",
                            value = """
                                    {
                                      "status": 404,
                                      "message": "읽음 상태를 찾을 수 없습니다."
                                    }
                                    """
                    )
            )
    )
    ReadStatusResponseDto update(
            @PathVariable UUID readStatusId,
            @RequestBody ReadStatusUpdateRequestDto request
    );

    @Operation(summary = "User의 Message 읽음 상태 목록 조회")
    @ApiResponse(responseCode = "200", description = "Message 읽음 상태 목록 조회 성공")
    List<ReadStatusResponseDto> findAllUserReadStatuses(
            @RequestParam UUID userId
    );
}
