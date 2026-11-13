package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.channel.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.channel.ChannelDto;
import com.sprint.mission.discodeit.service.ChannelService;

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
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    // 공개 채널 생성
    @ApiResponse(responseCode = "201", description = "공개 채널 생성 성공")
    @RequestMapping(value = "/public", method = RequestMethod.POST)
    public ResponseEntity<ChannelDto> createPublic(
            @RequestBody ChannelCreateRequest request
    ) {
        ChannelDto channelDto = channelService.createPublic(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(channelDto);
    }

    // 비공개 채널 생성
    @ApiResponse(responseCode = "201", description = "비공개 채널 생성 성공")
    @RequestMapping(value = "/private", method = RequestMethod.POST)
    public ResponseEntity<ChannelDto> createPrivate(
            @RequestBody PrivateChannelCreateRequest request
    ) {
        ChannelDto channelDto = channelService.createPrivate(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(channelDto);
    }

    // 특정 사용자가 볼 수 있는 채널 목록 조회
    @ApiResponse(responseCode = "200", description = "채널 목록 조회 성공")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ChannelDto>> findAllByUserId(
            @RequestParam UUID userId
    ) {
        return ResponseEntity.ok(
                channelService.findAllByUserId(userId)
        );
    }

    // 공개 채널 정보 수정
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "채널 정보 수정 성공"),
            @ApiResponse(
                    responseCode = "400",
                    description = "비공개 채널은 수정할 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = Map.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Channel을 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = Map.class)
                    )
            )
    })
    @RequestMapping(value = "/{channelId}", method = RequestMethod.PATCH)
    public ResponseEntity<ChannelDto> update(
            @PathVariable UUID channelId,
            @RequestBody ChannelUpdateRequest request
    ) {
        return ResponseEntity.ok(
                channelService.update(channelId, request)
        );
    }

    // 채널 삭제
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "채널 삭제 성공"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Channel을 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = Map.class)
                    )
            )
    })
    @RequestMapping(value = "/{channelId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(
            @PathVariable UUID channelId
    ) {
        channelService.delete(channelId);

        return ResponseEntity.noContent().build();
    }
}