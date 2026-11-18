package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Channel", description = "공개 및 비공개 채널 관리 API")
public interface ChannelApi {

    @Operation(summary = "공개 채널 생성", description = "새로운 공개(PUBLIC) 채널을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "공개 채널 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 본문")
    })
    @PostMapping("/public")
    ResponseEntity<Channel> createPublicChannel(@RequestBody PublicChannelCreateRequest request);

    @Operation(summary = "비공개 채널 생성", description = "참여자 ID 목록을 지정하여 비공개(PRIVATE) 채널을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "비공개 채널 생성 성공"),
            @ApiResponse(responseCode = "400", description = "참여자 목록이 비어있거나 null인 경우"),
            @ApiResponse(responseCode = "404", description = "참여자 목록 중 존재하지 않는 사용자 ID가 포함된 경우")
    })
    @PostMapping("/private")
    ResponseEntity<Channel> createPrivateChannel(@RequestBody PrivateChannelCreateRequest request);

    @Operation(summary = "사용자별 채널 목록 조회", description = "특정 사용자가 접근 가능한 모든 공개 채널 및 참여 중인 비공개 채널을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "채널 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "필수 파라미터(userId) 누락 또는 형식 오류")
    })
    @GetMapping
    ResponseEntity<List<ChannelDto>> findAllByUserId(
            @Parameter(description = "조회할 사용자 UUID") @RequestParam UUID userId);

    @Operation(summary = "공개 채널 정보 수정", description = "공개 채널의 이름 및 설명을 수정합니다. (비공개 채널은 수정 불가)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "채널 수정 성공"),
            @ApiResponse(responseCode = "400", description = "비공개(PRIVATE) 채널 수정을 시도한 경우"),
            @ApiResponse(responseCode = "404", description = "해당 ID의 채널을 찾을 수 없는 경우")
    })
    @PatchMapping("/{channelId}")
    ResponseEntity<Channel> updateChannel(
            @Parameter(description = "수정할 채널 UUID") @PathVariable UUID channelId,
            @RequestBody PublicChannelUpdateRequest request);

    @Operation(summary = "채널 삭제", description = "채널 및 해당 채널의 모든 메시지, 첨부파일, 읽음 상태를 함께 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "채널 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "삭제할 채널을 찾을 수 없는 경우")
    })
    @DeleteMapping("/{channelId}")
    ResponseEntity<Void> deleteChannel(
            @Parameter(description = "삭제할 채널 UUID") @PathVariable UUID channelId);
}