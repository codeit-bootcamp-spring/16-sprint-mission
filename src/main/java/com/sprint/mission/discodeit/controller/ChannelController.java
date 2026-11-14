package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.channels.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.request.channels.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channels.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.service.ChannelService;

import jakarta.validation.Valid; // DTO의 유효성 검사를 실행하도록 지정
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    // 공개 채널 생성(PUBLIC)-POST
    // 생성 성공 시 201 Created 반환
    @RequestMapping(value = "/public", method = RequestMethod.POST)
    public ResponseEntity<ChannelDto> postPublicChannel(
            @Valid @RequestBody PublicChannelCreateRequest request) {

        ChannelDto channel = channelService.createPublicChannel(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(channel);
    }

    // 비공개 채널 생성(PRIVATE)-POST
    // 생성 성공 시 201 Created 반환
    @RequestMapping(value = "/private", method = RequestMethod.POST)
    public ResponseEntity<ChannelDto> postPrivateChannel(
            @Valid @RequestBody PrivateChannelCreateRequest request) {

        ChannelDto channel = channelService.createPrivateChannel(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(channel);
    }

    // 특정 사용자가 볼 수 있는 모든 채널 조회(GET)
    // Query Parameter: userId
    @RequestMapping(method = RequestMethod.GET)
    public List<ChannelDto> getChannelByUserId(@RequestParam("userId") UUID userId) {
        return channelService.findAllByUserId(userId);
    }

    // 공개 채널 정보 수정(PATCH)
    // Path Variable: channelId
    // 다른 공개 채널과 이름이 중복되면 409 Conflict 반환
    @RequestMapping(value = "/{channelId}", method = RequestMethod.PATCH)
    public ChannelDto patchChannel(
            @PathVariable UUID channelId,
            @Valid @RequestBody PublicChannelUpdateRequest request) {

        return channelService.updateChannel(channelId, request);
    }

    // 채널 삭제(DELETE)
    // 삭제 성공 시 204 No Content 반환
    @RequestMapping(value = "/{channelId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteChannel(@PathVariable UUID channelId) {

        channelService.deleteChannel(channelId);
        return ResponseEntity.noContent().build();
    }
}