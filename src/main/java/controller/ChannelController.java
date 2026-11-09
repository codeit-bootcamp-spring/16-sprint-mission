package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
public class ChannelController {

    private final ChannelService channelService;

    // 1. 공개 채널 생성
    @RequestMapping(
            value = "/public",
            method = RequestMethod.POST
    )
    public Channel createPublic(
            @RequestBody PublicChannelCreateRequest request
    ) {
        return channelService.create(request);
    }

    // 2. 비공개 채널 생성
    @RequestMapping(
            value = "/private",
            method = RequestMethod.POST
    )
    public Channel createPrivate(
            @RequestBody PrivateChannelCreateRequest request
    ) {
        return channelService.create(request);
    }

    // 3. 공개 채널 정보 수정
    @RequestMapping(
            value = "/{channelId}",
            method = RequestMethod.PATCH
    )
    public Channel update(
            @PathVariable UUID channelId,
            @RequestBody PublicChannelUpdateRequest request
    ) {
        return channelService.update(channelId, request);
    }

    // 4. 채널 삭제
    @RequestMapping(
            value = "/{channelId}",
            method = RequestMethod.DELETE
    )
    public void delete(@PathVariable UUID channelId) {
        channelService.delete(channelId);
    }

    // 5. 특정 사용자가 볼 수 있는 채널 목록 조회
    @RequestMapping(method = RequestMethod.GET)
    public List<ChannelDto> findAllByUserId(
            @RequestParam UUID userId
    ) {
        return channelService.findAllByUserId(userId);
    }
}
