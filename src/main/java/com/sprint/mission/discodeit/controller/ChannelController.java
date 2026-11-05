package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.*;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("v1/channels")
public class ChannelController {
    private final ChannelService channelService;

    @PostMapping("/public")
    public ResponseEntity<ChannelPublicCreateResponse> createPublicChannel(@RequestBody ChannelPublicCreateRequest createRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.channelService.createPublic(createRequest));
    }

    @PostMapping("/private")
    public ResponseEntity<ChannelPrivateCreateResponse> createPrivateChannel(@RequestBody ChannelPrivateCreateRequest createRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.channelService.createPrivate(createRequest));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ChannelUpdateResponse> updatePublicChannel(@PathVariable UUID id,
                                                                     @RequestBody ChannelUpdateRequest updateRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(this.channelService.update(id, updateRequest));
    }

    @GetMapping
    public ResponseEntity<List<ChannelFindResponse>> findAllChannels(@RequestParam UUID userId) {
        return ResponseEntity.status(HttpStatus.OK).body(this.channelService.findAllByUserId(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChannel(@PathVariable UUID id) {
        this.channelService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
