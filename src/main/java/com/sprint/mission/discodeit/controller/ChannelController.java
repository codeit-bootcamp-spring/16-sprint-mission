package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.*;
import com.sprint.mission.discodeit.service.ChannelService;
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
@RequestMapping("/api/channels")
@RequiredArgsConstructor
@Tag(
        name = "Channel",
        description = "공개 및 비공개 채널의 생성, 수정, 조회 및 삭제 API"
)
public class ChannelController {

    private final ChannelService channelService;

    @PostMapping("/public")
    @Operation(
            summary = "Public Channel 생성",
            description = "채널 이름과 설명을 이용하여 공개 채널을 생성합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Public Channel이 성공적으로 생성되었습니다.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ChannelDto.class),
                            examples = @ExampleObject(
                                    value = """
                        {
                          "id": "550e8400-e29b-41d4-a716-446655440000",
                          "createdAt": "2026-11-17T06:00:00Z",
                          "updatedAt": "2026-11-17T06:00:00Z",
                          "type": "PUBLIC",
                          "name": "general",
                          "description": "자유롭게 대화하는 채널입니다."
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "채널 이름의 유효성 검증 실패 또는 잘못된 요청 형식",
                    content = {
                            @Content(
                                    mediaType = MediaType.TEXT_PLAIN_VALUE,
                                    schema = @Schema(type = "string"),
                                    examples = @ExampleObject(
                                            value = "요청값이 올바르지 않습니다."
                                    )
                            ),
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(type = "object")
                            )
                    }
            )
    })
    public ResponseEntity<ChannelDto> createPublicChannel(
            @Valid @RequestBody PublicChannelCreateRequest request
    ) {
        ChannelDto publicChannelDto =
                this.channelService.createPublicChannel(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(publicChannelDto);
    }

    @PostMapping("/private")
    @Operation(
            summary = "Private Channel 생성",
            description = "참여할 User의 UUID 목록을 이용하여 비공개 채널을 생성하고 참여자의 읽음 상태 정보를 생성합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Private Channel이 성공적으로 생성되었습니다.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ChannelDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "참여자 목록의 유효성 검증 실패 또는 잘못된 요청 형식",
                    content = {
                            @Content(
                                    mediaType = MediaType.TEXT_PLAIN_VALUE,
                                    schema = @Schema(type = "string"),
                                    examples = @ExampleObject(
                                            value = "요청값이 올바르지 않습니다."
                                    )
                            ),
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(type = "object")
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "참여자로 지정한 User 중 존재하지 않는 사용자가 있습니다.",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(type = "string"),
                            examples = @ExampleObject(
                                    value = "프라이빗 채널에 추가하려는 사용자 중 존재하지 않는 사용자가 있습니다."
                            )
                    )
            )
    })
    public ResponseEntity<ChannelDto> createPrivateChannel(
            @Valid @RequestBody PrivateChannelCreateRequest request
    ) {
        ChannelDto privateChannelDto =
                this.channelService.createPrivateChannel(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(privateChannelDto);
    }

    @PatchMapping("/{channelId}")
    @Operation(
            summary = "Public Channel 수정",
            description = "지정한 공개 채널의 이름 또는 설명을 수정합니다. 비공개 채널은 수정할 수 없습니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Public Channel이 성공적으로 수정되었습니다.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ChannelDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 채널 이름, 비공개 채널 수정 요청, 잘못된 UUID 또는 요청 형식",
                    content = {
                            @Content(
                                    mediaType = MediaType.TEXT_PLAIN_VALUE,
                                    schema = @Schema(type = "string"),
                                    examples = {
                                            @ExampleObject(
                                                    name = "잘못된 채널 이름",
                                                    value = "채널 이름은 공백으로만 구성할 수 없습니다."
                                            ),
                                            @ExampleObject(
                                                    name = "비공개 채널 수정",
                                                    value = "프라이빗 채널은 수정할 수 없습니다."
                                            )
                                    }
                            ),
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(type = "object")
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "수정할 Channel이 존재하지 않습니다.",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(type = "string")
                    )
            )
    })
    public ResponseEntity<ChannelDto> updatePublicChannel(
            @PathVariable("channelId") UUID channelId,
            @RequestBody ChannelUpdateRequest request
    ) {
        ChannelDto channelDto = this.channelService.update(channelId, request);

        return ResponseEntity.ok(channelDto);
    }

    @DeleteMapping("/{channelId}")
    @Operation(
            summary = "Channel 삭제",
            description = "지정한 채널과 해당 채널의 메시지, 첨부파일 및 읽음 상태 정보를 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Channel이 성공적으로 삭제되었습니다. 응답 본문은 없습니다.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "channelId의 UUID 형식이 올바르지 않습니다.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(type = "object")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "삭제할 Channel이 존재하지 않습니다.",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(type = "string")
                    )
            )
    })
    public ResponseEntity<Void> deleteChannel(
            @PathVariable("channelId") UUID channelId
    ) {
        this.channelService.delete(channelId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(
            summary = "User별 Channel 목록 조회",
            description = "지정한 User가 참여 중인 비공개 채널과 모든 공개 채널을 조회하여 JSON 배열로 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Channel 목록 조회에 성공했습니다.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(
                                    schema = @Schema(implementation = ChannelResponse.class)
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "userId 누락 또는 UUID 형식 오류",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(type = "object")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "해당 User가 존재하지 않습니다.",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(type = "string")
                    )
            )
    })
    public ResponseEntity<List<ChannelResponse>> getVisibleChannels(
            @RequestParam("userId") UUID userId
    ) {
        ChannelListResponse channelListResponse =
                this.channelService.findAllByUserId(userId);

        return ResponseEntity.ok(channelListResponse.channels());
    }
}