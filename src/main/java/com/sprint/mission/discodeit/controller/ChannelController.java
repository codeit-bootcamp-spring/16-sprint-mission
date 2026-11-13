package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequestDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequestDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequestDto;
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
public class ChannelController {

    private final ChannelService channelService;

    @PostMapping("/public")
    @ResponseStatus(HttpStatus.CREATED)
    public ChannelResponseDto createPublic(
            @RequestBody @Valid PublicChannelCreateRequestDto request
    ) {
        return channelService.createPublicChannel(request);
    }

    @PostMapping("/private")
    public ChannelResponseDto createPrivate(
            @RequestBody @Valid PrivateChannelCreateRequestDto request
    ) {
        return channelService.createPrivateChannel(request);
    }

    @GetMapping
    public List<ChannelResponseDto> findAccessible(
            @RequestHeader("USER-UUID") UUID userId
    ) {
        return channelService.findAccessibleChannelsByUserId(userId);
    }

    @PatchMapping("/{channelId}")
    public ChannelResponseDto update(
            @PathVariable UUID channelId,
            @RequestBody ChannelUpdateRequestDto request
    ) {
        return channelService.updateChannel(channelId, request);
    }

    @DeleteMapping("/{channelId}")
    public void delete(
            @PathVariable UUID channelId
    ) {
        channelService.deleteChannel(channelId);
    }
}
