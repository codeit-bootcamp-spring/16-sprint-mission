package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    // 공개 채널 생성
    @RequestMapping(path = "/public", method = RequestMethod.POST)
    public ResponseEntity<Channel> create(@RequestBody PublicChannelCreateRequest request) {
        Channel createdChannel = channelService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel);
    }

    // 비공개 채널 생성
    @RequestMapping(path = "/private", method = RequestMethod.POST)
    public ResponseEntity<Channel> create(@RequestBody PrivateChannelCreateRequest request) {
        Channel createdChannel = channelService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel);
    }

    // 공개 채널 수정
    @RequestMapping(path = "/{channelId}", method = RequestMethod.PATCH)
    public ResponseEntity<Channel> update(
            @PathVariable("channelId") UUID channelId,
            @RequestBody PublicChannelUpdateRequest request
    ) {
        Channel updatedChannel = channelService.update(channelId, request);
        return ResponseEntity.status(HttpStatus.OK).body(updatedChannel);
    }

    // 채널 삭제
    @RequestMapping(path = "/{channelId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable("channelId") UUID channelId) {
        channelService.delete(channelId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 특정 유저가 볼 수 있는 채널 목록 조회
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ChannelDto>> findAll(@RequestParam("userId") UUID userId) {
        List<ChannelDto> channels = channelService.findAllByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(channels);
    }
}