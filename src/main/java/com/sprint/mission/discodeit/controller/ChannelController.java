package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.*;
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("api/channels")
public class ChannelController {
    private final ChannelService channelService;

    @RequestMapping(value = "/public", method = RequestMethod.POST)
    public ResponseEntity<ChannelResponse> createPublicChannel(@Valid @RequestBody PublicChannelCreateRequest createRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.channelService.createPublic(createRequest));
    }

    @RequestMapping(value = "/private", method = RequestMethod.POST)
    public ResponseEntity<ChannelResponse> createPrivateChannel(@Valid @RequestBody PrivateChannelCreateRequest createRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.channelService.createPrivate(createRequest));
    }

    @RequestMapping(value = "/{id}/users", method = RequestMethod.POST)
    public ResponseEntity<Void> joinChannel(@NotNull @PathVariable UUID id,
                                            @Valid @RequestBody ChannelJoinRequest joinRequest) {
        this.channelService.addUserToChannel(id, joinRequest.userId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @RequestMapping(value = "/{id}/users", method = RequestMethod.DELETE)
    public ResponseEntity<Void> leaveChannel(@NotNull @PathVariable UUID id,
                                             @Valid @RequestBody ChannelLeaveRequest leaveRequest) {
        this.channelService.removeUserFromChannel(id, leaveRequest.userId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @RequestMapping(value = "/{channelId}", method = RequestMethod.PATCH)
    public ResponseEntity<ChannelResponse> updatePublicChannel(@NotNull @PathVariable UUID channelId,
                                                               @RequestBody PublicChannelUpdateRequest updateRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(this.channelService.update(channelId, updateRequest));
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ChannelFindResponse>> findAllChannels(@NotNull @RequestParam UUID userId) {
        return ResponseEntity.status(HttpStatus.OK).body(this.channelService.findAllByUserId(userId));
    }

    @RequestMapping(value = "/{channelId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteChannel(@NotNull @PathVariable UUID channelId) {
        this.channelService.delete(channelId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
