package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.channel.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.channel.ChannelDto;
import com.sprint.mission.discodeit.service.ChannelService;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "Channel", description = "채널 생성, 조회, 수정 및 삭제 API")
@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    @Operation(
            summary = "공개 채널 생성",
            description = "새로운 공개 채널을 생성합니다. 생성된 채널 정보를 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "공개 채널 생성 성공"),
            @ApiResponse(
                    responseCode = "400",
                    description = "요청 데이터가 올바르지 않음",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @RequestMapping(value = "/public", method = RequestMethod.POST)
    public ResponseEntity<ChannelDto> createPublic(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "공개 채널 생성 정보",
                    required = true
            )
            @RequestBody ChannelCreateRequest request
    ) {
        ChannelDto channelDto = channelService.createPublic(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(channelDto);
    }

    @Operation(
            summary = "비공개 채널 생성",
            description = "지정한 참여자들만 접근할 수 있는 비공개 채널을 생성합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "비공개 채널 생성 성공"),
            @ApiResponse(
                    responseCode = "400",
                    description = "요청 데이터가 올바르지 않음",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @RequestMapping(value = "/private", method = RequestMethod.POST)
    public ResponseEntity<ChannelDto> createPrivate(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "비공개 채널 생성 정보 및 참여자 정보",
                    required = true
            )
            @RequestBody PrivateChannelCreateRequest request
    ) {
        ChannelDto channelDto = channelService.createPrivate(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(channelDto);
    }

    @Operation(
            summary = "사용자가 참여 가능한 채널 목록 조회",
            description = "공개 채널과 해당 사용자가 참여 중인 비공개 채널 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "채널 목록 조회 성공"),
            @ApiResponse(
                    responseCode = "400",
                    description = "사용자 ID 형식이 올바르지 않음",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ChannelDto>> findAllByUserId(
            @Parameter(
                    description = "조회할 사용자 ID (UUID)",
                    required = true,
                    example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
            )
            @RequestParam UUID userId
    ) {
        return ResponseEntity.ok(
                channelService.findAllByUserId(userId)
        );
    }

    @Operation(
            summary = "공개 채널 정보 수정",
            description = "공개 채널의 이름 또는 설명을 수정합니다. "
                    + "newName과 newDescription 중 하나만 전달하거나 두 항목을 함께 전달할 수 있습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "채널 정보 수정 성공"),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청이거나 비공개 채널 수정 시도",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 채널",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @RequestMapping(value = "/{channelId}", method = RequestMethod.PATCH)
    public ResponseEntity<ChannelDto> update(
            @Parameter(
                    description = "수정할 채널 ID (UUID)",
                    required = true,
                    example = "4fd2e346-ad09-41cd-a879-336ed7915f46"
            )
            @PathVariable UUID channelId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "수정할 채널 정보. 이름과 설명은 선택적으로 전달할 수 있습니다.",
                    required = true
            )
            @RequestBody ChannelUpdateRequest request
    ) {
        return ResponseEntity.ok(
                channelService.update(channelId, request)
        );
    }

    @Operation(
            summary = "채널 삭제",
            description = "지정한 채널을 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "채널 삭제 성공"),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 채널",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @RequestMapping(value = "/{channelId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(
            @Parameter(
                    description = "삭제할 채널 ID (UUID)",
                    required = true,
                    example = "4fd2e346-ad09-41cd-a879-336ed7915f46"
            )
            @PathVariable UUID channelId
    ) {
        channelService.delete(channelId);

        return ResponseEntity.noContent().build();
    }
}