package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ChannelApi;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
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
public class ChannelController implements ChannelApi {
    private final ChannelService channelService;

    @Override
    public ResponseEntity<Channel> createPublicChannel(PublicChannelCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(channelService.create(request));
    }

    @Override
    public ResponseEntity<Channel> createPrivateChannel(PrivateChannelCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(channelService.create(request));
    }

    @Override
    public ResponseEntity<List<ChannelDto>> findAllByUserId(UUID userId) {
        return ResponseEntity.ok(channelService.findAllByUserId(userId));
    }

    @Override
    public ResponseEntity<Channel> updateChannel(UUID channelId, PublicChannelUpdateRequest request) {
        return ResponseEntity.ok(channelService.update(channelId, request));
    }

    @Override
    public ResponseEntity<Void> deleteChannel(UUID channelId) {
        channelService.delete(channelId);
        return ResponseEntity.noContent().build();
    }
}