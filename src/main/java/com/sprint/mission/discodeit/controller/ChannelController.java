package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.*;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/channels")
@RequiredArgsConstructor
public class ChannelController {
    private final ChannelService channelService;

    @PostMapping
    public ResponseEntity<ChannelInfo> createChannel(@RequestBody ChannelCreateRequest request) {
        ChannelInfo channelInfo = this.channelService.createChannel(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(channelInfo);
    }

    @PatchMapping("/{channel-id}")
    public ResponseEntity<ChannelInfo> updatePublicChannel(@PathVariable("channel-id") UUID channelId, @RequestBody ChannelUpdateRequest request) {
        ChannelInfo channelInfo = this.channelService.update(channelId, request);

        return ResponseEntity.ok(channelInfo);
    }

    @DeleteMapping("/{channel-id}")
    public ResponseEntity<ChannelInfo> deleteChannel(@PathVariable("channel-id") UUID channelId) {
        this.channelService.delete(channelId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{user-id}")
    public ResponseEntity<ChannelInfoList> getVisibleChannels(@PathVariable("user-id") UUID userId) {
        List<ChannelInfo> channelInfoList = this.channelService.findAllByUserId(userId);
        ChannelInfoList channelInfoListDto = new ChannelInfoList(channelInfoList);

        return ResponseEntity.ok(channelInfoListDto);
    }
}
