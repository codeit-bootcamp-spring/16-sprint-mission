package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.*;
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

    /*
    @PostMapping({"/public", "/private"})
    public ResponseEntity<ChannelResponse> createChannel(
            @RequestBody ChannelCreateRequest request
    ) {
        ChannelResponse channelResponse = this.channelService.createChannel(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(channelResponse);
    }
     */

    @PostMapping("/public")
    public ResponseEntity<ChannelDto> createPublicChannel(
            @RequestBody PublicChannelCreateRequest request
    ) {
        ChannelDto publicChannelDto =
                this.channelService.createPublicChannel(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(publicChannelDto);
    }

    @PostMapping("/private")
    public ResponseEntity<ChannelDto> createPrivateChannel(
            @RequestBody PrivateChannelCreateRequest request
    ) {
        ChannelDto privateChannelDto =
                this.channelService.createPrivateChannel(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(privateChannelDto);
    }

    @PatchMapping("/{channelId}")
    public ResponseEntity<ChannelDto> updatePublicChannel(
            @PathVariable("channelId") UUID channelId,
            @RequestBody ChannelUpdateRequest request
    ) {
        ChannelDto channelDto = this.channelService.update(channelId, request);

        return ResponseEntity.ok(channelDto);
    }

    @DeleteMapping("/{channelId}")
    public ResponseEntity<Void> deleteChannel(
            @PathVariable("channelId") UUID channelId
    ) {
        this.channelService.delete(channelId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ChannelResponse>> getVisibleChannels(
            @RequestParam("userId") UUID userId
    ) {
        ChannelListResponse channelListResponse =
                this.channelService.findAllByUserId(userId);

        return ResponseEntity.ok(channelListResponse.channels());
    }
}
