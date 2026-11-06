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

    @RequestMapping(value = "/public", method = RequestMethod.POST)
    public ResponseEntity<ChannelPublicCreateResponse> createPublicChannel(@RequestBody ChannelPublicCreateRequest createRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.channelService.createPublic(createRequest));
    }

    @RequestMapping(value = "/private", method = RequestMethod.POST)
    public ResponseEntity<ChannelPrivateCreateResponse> createPrivateChannel(@RequestBody ChannelPrivateCreateRequest createRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.channelService.createPrivate(createRequest));
    }

    @RequestMapping(value = "/{id}/users", method = RequestMethod.POST)
    public ResponseEntity<Void> joinChannel(@PathVariable UUID id,
                                            @RequestBody ChannelJoinRequest joinRequest) {
        this.channelService.addUserToChannel(id, joinRequest.userId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @RequestMapping(value = "/{id}/users", method = RequestMethod.DELETE)
    public ResponseEntity<Void> leaveChannel(@PathVariable UUID id,
                                             @RequestBody ChannelLeaveRequest leaveRequest) {
        this.channelService.removeUserFromChannel(id, leaveRequest.userId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<ChannelUpdateResponse> updatePublicChannel(@PathVariable UUID id,
                                                                     @RequestBody ChannelUpdateRequest updateRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(this.channelService.update(id, updateRequest));
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ChannelFindResponse>> findAllChannels(@RequestParam UUID userId) {
        return ResponseEntity.status(HttpStatus.OK).body(this.channelService.findAllByUserId(userId));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteChannel(@PathVariable UUID id) {
        this.channelService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
