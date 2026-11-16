package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Channel", description = "공개·비공개 채널 생성과 채널 조회, 수정, 삭제")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
public class ChannelController {

    private final ChannelService channelService;

    @Operation(summary = "공개 채널 생성")
    @ApiResponse(responseCode = "201", description = "공개 채널 생성 성공")
    @ApiResponse(
            responseCode = "400",
            description = "요청 JSON이 누락되었거나 형식이 올바르지 않음",
            content = @Content(schema = @Schema(implementation = String.class))
    )
    @ApiResponse(
            responseCode = "415",
            description = "요청 본문의 Content-Type을 지원하지 않음",
            content = @Content(schema = @Schema(implementation = String.class))
    )
    @PostMapping(value = "/public")
    public ResponseEntity<Channel> create(@RequestBody PublicChannelCreateRequest request) {
        Channel createdChannel = channelService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdChannel);
    }

    @Operation(summary = "비공개 채널 생성")
    @ApiResponse(responseCode = "201", description = "비공개 채널 생성 성공")
    @ApiResponse(
            responseCode = "400",
            description = "요청 JSON이 누락되었거나 형식이 올바르지 않음",
            content = @Content(schema = @Schema(implementation = String.class))
    )
    @ApiResponse(
            responseCode = "415",
            description = "요청 본문의 Content-Type을 지원하지 않음",
            content = @Content(schema = @Schema(implementation = String.class))
    )
    @PostMapping(value = "/private")
    public ResponseEntity<Channel> create(@RequestBody PrivateChannelCreateRequest request) {
        Channel createdChannel = channelService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdChannel);
    }

    @Operation(summary = "공개 채널 수정")
    @ApiResponse(responseCode = "200", description = "채널 수정 성공")
    @ApiResponse(
            responseCode = "400",
            description = "채널 ID 또는 요청 JSON 형식이 잘못되었거나 비공개 채널을 수정하려 함",
            content = @Content(schema = @Schema(implementation = String.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "수정할 채널을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = String.class))
    )
    @ApiResponse(
            responseCode = "415",
            description = "요청 본문의 Content-Type을 지원하지 않음",
            content = @Content(schema = @Schema(implementation = String.class))
    )
    @PatchMapping(value = "/{channelId}")
    public ResponseEntity<Channel> update(@PathVariable("channelId") UUID channelId,
                                          @RequestBody PublicChannelUpdateRequest request) {
        Channel udpatedChannel = channelService.update(channelId, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(udpatedChannel);
    }

    @Operation(summary = "채널 삭제")
    @ApiResponse(responseCode = "204", description = "채널 삭제 성공", content = @Content)
    @ApiResponse(
            responseCode = "400",
            description = "채널 ID의 UUID 형식이 올바르지 않음",
            content = @Content(schema = @Schema(implementation = String.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "삭제할 채널을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = String.class))
    )
    @DeleteMapping(value = "/{channelId}")
    public ResponseEntity<Void> delete(@PathVariable("channelId") UUID channelId) {
        channelService.delete(channelId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @Operation(
            summary = "사용자에게 보이는 채널 목록 조회",
            description = "공개 채널과 해당 사용자가 참여한 비공개 채널을 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "채널 목록 조회 성공")
    @ApiResponse(
            responseCode = "400",
            description = "userId가 누락되었거나 UUID 형식이 올바르지 않음",
            content = @Content(schema = @Schema(implementation = String.class))
    )
    @GetMapping
    public ResponseEntity<List<ChannelDto>> findAll(@RequestParam("userId") UUID userId) {
        List<ChannelDto> channels = channelService.findAllByUserId(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(channels);
    }
}
