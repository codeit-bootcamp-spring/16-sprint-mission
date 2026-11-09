package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.channel.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.channel.ChannelDto;
import com.sprint.mission.discodeit.service.ChannelService;

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

    // 공개 채널 생성
    @RequestMapping(
            value = "/public",
            method = RequestMethod.POST
    )
    public ResponseEntity<ChannelDto> createPublic(
            @RequestBody ChannelCreateRequest request
    ) {
        ChannelDto channelDto = channelService.createPublic(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(channelDto);
    }

    // 비공개 채널 생성
    @RequestMapping(
            value = "/private",
            method = RequestMethod.POST
    )
    public ResponseEntity<ChannelDto> createPrivate(
            @RequestBody PrivateChannelCreateRequest request
    ) {
        ChannelDto channelDto = channelService.createPrivate(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(channelDto);
    }

    // 특정 사용자가 볼 수 있는 채널 목록 조회
    @RequestMapping(
            value = "/users/{userId}",
            method = RequestMethod.GET
    )
    public ResponseEntity<List<ChannelDto>> findAllByUserId(
            @PathVariable UUID userId
    ) {
        return ResponseEntity.ok(
                channelService.findAllByUserId(userId)
        );
    }

    // 공개 채널 정보 수정
    @RequestMapping(
            value = "/{channelId}",
            method = RequestMethod.PATCH
    )
    public ResponseEntity<ChannelDto> update(
            @PathVariable UUID channelId,
            @RequestBody ChannelUpdateRequest request
    ) {
        ChannelUpdateRequest updateRequest = new ChannelUpdateRequest(
                channelId,
                request.name(),
                request.description()
        );

        return ResponseEntity.ok(
                channelService.update(updateRequest)
        );
    }

    // 채널 삭제
    @RequestMapping(
            value = "/{channelId}",
            method = RequestMethod.DELETE
    )
    public ResponseEntity<Void> delete(
            @PathVariable UUID channelId
    ) {
        channelService.delete(channelId);

        return ResponseEntity.noContent().build();
    }
}