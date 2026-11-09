package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
public class ChannelController {
    private final ChannelService channelService;

    @RequestMapping(value = "/public", method = RequestMethod.POST)
    public ResponseEntity<ChannelDto> createPublic(@RequestBody PublicChannelCreateRequest request) {
        ChannelDto channel = channelService.createPublic(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(channel);
    }

    @RequestMapping(value = "/private", method = RequestMethod.POST)
    public ResponseEntity<ChannelDto> createPrivate(@RequestBody PrivateChannelCreateRequest request) {
        ChannelDto channel = channelService.createPrivate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(channel);
    }

    @RequestMapping(value = "/{channelId}", method = RequestMethod.PATCH)
    public ResponseEntity<ChannelDto> update(@PathVariable UUID channelId,
                                             @RequestBody ChannelUpdateRequest request) {
        return ResponseEntity.ok(channelService.update(channelId, request));
    }

    @RequestMapping(value = "/{channelId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable UUID channelId,
                                       @RequestParam UUID requesterId) {
        channelService.delete(channelId, requesterId);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ChannelDto>> findAllByUserId(@RequestParam UUID userId) {
        return ResponseEntity.ok(channelService.findAllByUserId(userId));
    }
}
