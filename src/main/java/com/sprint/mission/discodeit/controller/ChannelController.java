package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.request.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.request.UpdateChannelRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.dto.response.PrivateChannelResponse;
import com.sprint.mission.discodeit.dto.response.FindChannelResponse;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.dto.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Channel", description = "채널 API")
@RestController
@AllArgsConstructor
@RequestMapping("/api/channels")
public class ChannelController {
    private final ChannelService channelService;

    @Operation(summary = "공개 채널 생성", description = "누구나 볼 수 있는 공개 채널을 생성합니다.")
    @ApiResponse(responseCode = "201", description = "생성 성공")
    @PostMapping("/public")
    public ResponseEntity<ChannelResponse> createPublicChannel(@RequestBody CreatePublicChannelRequest request) {
        ChannelResponse response = channelService.createPublicChannel(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "비공개 채널 생성", description = "참여자만 볼 수 있는 비공개 채널을 생성합니다.")
    @ApiResponse(responseCode = "201", description = "생성 성공")
    @ApiResponse(responseCode = "400", description = "참여자 목록이 유효하지 않음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "참여자 중 존재하지 않는 사용자가 있음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping("/private")
    public ResponseEntity<PrivateChannelResponse> createPrivateChannel(@RequestBody CreatePrivateChannelRequest request) {
        PrivateChannelResponse response = channelService.createPrivateChannel(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "채널 수정", description = "공개 채널의 이름과 설명을 수정합니다. 비공개 채널은 수정할 수 없습니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @ApiResponse(responseCode = "400", description = "비공개 채널은 수정할 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "해당 채널이 존재하지 않음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PatchMapping("/{channelId}")
    public ResponseEntity<ChannelResponse> updateChannel(@RequestBody UpdateChannelRequest request,
                                                         @PathVariable UUID channelId) {
        ChannelResponse response = channelService.update(channelId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "채널 삭제", description = "채널을 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "삭제 성공", content = @Content)
    @ApiResponse(responseCode = "404", description = "해당 채널이 존재하지 않음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @DeleteMapping("/{channelId}")
    public ResponseEntity deleteChannel(@PathVariable UUID channelId) {
        channelService.delete(channelId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "채널 목록 조회", description = "해당 사용자가 볼 수 있는 채널(공개 채널 + 참여 중인 비공개 채널)을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public ResponseEntity<List<FindChannelResponse>> getChannels(@RequestParam UUID userId) {
        List<FindChannelResponse> response = channelService.findAllByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
