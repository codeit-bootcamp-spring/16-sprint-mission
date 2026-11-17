package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequestDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequestDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequestDto;
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

@Tag(name = "Channel", description = "Channel API")
public interface ChannelApi {

    @Operation(summary = "Public Channel 생성")
    @ApiResponse(responseCode = "201", description = "Public Channel이 성공적으로 생성됨")
    ChannelResponseDto createPublic(
            @Valid @RequestBody PublicChannelCreateRequestDto request
    );

    @Operation(summary = "Private Channel 생성")
    @ApiResponse(responseCode = "201", description = "Private Channel이 성공적으로 생성됨")
    @ApiResponse(
            responseCode = "404",
            description = "참여자를 찾을 수 없음",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                            name = "참여자를 찾을 수 없음",
                            value = """
                                    {
                                      "status": 404,
                                      "message": "사용자를 찾을 수 없습니다. %s"
                                    }
                                    """
                    )
            )
    )
    ChannelResponseDto createPrivate(
            @Valid @RequestBody PrivateChannelCreateRequestDto request
    );

    @Operation(summary = "User가 참여 중인 Channel 목록 조회")
    @ApiResponse(responseCode = "200", description = "Channel 목록 조회 성공")
    @ApiResponse(
            responseCode = "404",
            description = "User를 찾을 수 없음",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                            name = "User를 찾을 수 없음",
                            value = """
                                    {
                                      "status": 404,
                                      "message": "사용자를 찾을 수 없습니다. %s"
                                    }
                                    """
                    )
            )
    )
    List<ChannelDto> findAccessible(
            @RequestParam UUID userId
    );

    @Operation(summary = "Channel 정보 수정")
    @ApiResponse(responseCode = "200", description = "Channel 정보가 성공적으로 수정됨")
    @ApiResponse(
            responseCode = "400",
            description = "Private Channel은 수정할 수 없음",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                            name = "비공개 채널 수정 불가",
                            value = """
                                    {
                                      "status": 400,
                                      "message": "비공개 채널은 수정할 수 없습니다."
                                    }
                                    """
                    )
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Channel을 찾을 수 없음",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                            name = "Channel을 찾을 수 없음",
                            value = """
                                    {
                                      "status": 404,
                                      "message": "채널을 찾을 수 없습니다. %s"
                                    }
                                    """
                    )
            )
    )
    ChannelResponseDto update(
            @PathVariable UUID channelId,
            @RequestBody ChannelUpdateRequestDto request
    );

    @Operation(summary = "Channel 삭제")
    @ApiResponse(responseCode = "204", description = "Channel이 성공적으로 삭제됨")
    @ApiResponse(
            responseCode = "404",
            description = "Channel을 찾을 수 없음",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                            name = "Channel을 찾을 수 없음",
                            value = """
                                    {
                                      "status": 404,
                                      "message": "채널을 찾을 수 없습니다. %s"
                                    }
                                    """
                    )
            )
    )
    void delete(
            @PathVariable UUID channelId
    );
}
