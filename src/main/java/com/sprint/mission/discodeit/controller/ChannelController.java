package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequestDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequestDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequestDto;
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

    @RequestMapping(value = "/public", method = RequestMethod.POST)
    public ChannelResponseDto createPublic(
            @RequestHeader("USER-UUID") UUID userId,
            @RequestBody PublicChannelCreateRequestDto request
    ) {
        return channelService.createPublicChannel(userId, request);
    }

    @RequestMapping(value = "/private", method = RequestMethod.POST)
    public ChannelResponseDto createPrivate(
            @RequestHeader("USER-UUID") UUID userId,
            @RequestBody PrivateChannelCreateRequestDto request
    ) {
        return channelService.createPrivateChannel(userId, request);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<ChannelResponseDto> findAccessible(
            @RequestHeader("USER-UUID") UUID userId
    ) {
        return channelService.findAccessibleChannelsByUserId(userId);
    }

    @RequestMapping(method = RequestMethod.PATCH)
    public ChannelResponseDto update(
            @RequestHeader("CHANNEL-UUID") UUID channelId,
            @RequestBody ChannelUpdateRequestDto request
    ) {
        return channelService.updateChannel(channelId, request);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public void delete(
            @RequestHeader("CHANNEL-UUID") UUID channelId
    ) {
        channelService.deleteChannel(channelId);
    }

    @RequestMapping(value = "/join", method = RequestMethod.POST)
    public ChannelResponseDto join(
            @RequestHeader("CHANNEL-UUID") UUID channelId,
            @RequestHeader("USER-UUID") UUID userId
    ) {
        return channelService.joinChannel(channelId, userId);
    }

    @RequestMapping(value = "/leave", method = RequestMethod.DELETE)
    public void leave(
            @RequestHeader("CHANNEL-UUID") UUID channelId,
            @RequestHeader("USER-UUID") UUID userId) {
        channelService.leaveChannel(channelId, userId);
    }
}
