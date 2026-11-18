package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.request.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.request.UpdateChannelRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.dto.response.CreatePrivateChannelResponse;
import com.sprint.mission.discodeit.dto.response.FindChannelResponse;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("api/channel")
public class ChannelController {
    private final ChannelService channelService;

    @PostMapping("/public")
    public ResponseEntity<ChannelResponse> createPublicChannel(@RequestBody CreatePublicChannelRequest request) {
        ChannelResponse response = channelService.createPublicChannel(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/private")
    public ResponseEntity<CreatePrivateChannelResponse> createPrivateChannel(@RequestBody CreatePrivateChannelRequest request) {
        CreatePrivateChannelResponse response = channelService.createPrivateChannel(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/update")
    public ResponseEntity<ChannelResponse> updateChannel(@RequestBody UpdateChannelRequest request) {
        ChannelResponse response = channelService.update(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{channel-id}")
    public ResponseEntity deleteChannel(@PathVariable("channel-id") UUID channelId) {
        channelService.delete(channelId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/user/{user-id}")
    public ResponseEntity<List<FindChannelResponse>> getChannels(@PathVariable("user-id") UUID userId) {
        List<FindChannelResponse> response = channelService.findAllByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{channelId}")
    public ResponseEntity<FindChannelResponse> getChannel(@PathVariable UUID channelId) {
        FindChannelResponse response = channelService.find(channelId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
