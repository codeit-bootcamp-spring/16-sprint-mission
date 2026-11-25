package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {
    private final ChannelService channelService;

    @RequestMapping(
            value = "/public",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ChannelResponse> createChannel(
            @RequestBody PublicChannelCreateRequest request
    ) {
        ChannelDto result = channelService.createPublic(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ChannelResponse(result));
    }

    @RequestMapping(
            value = "/private",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ChannelResponse> createPrivate(
            @RequestBody PrivateChannelBody request
    ) {
        ChannelDto result = channelService.createPrivate(new PrivateChannelCreateRequest(request.participantIds()));

        return ResponseEntity.status(HttpStatus.CREATED).body(new ChannelResponse(result));
    }

    @RequestMapping(
            value = "/{channelId}",
            method = RequestMethod.PATCH,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ChannelResponse> update(
            @PathVariable("channelId") UUID channelId,
            @RequestBody PublicChannelUpdateBody request
    ) {
        ChannelUpdateRequest serviceRequest = new ChannelUpdateRequest(
                channelId,
                request.newName(),
                request.newDescription()
        );
        channelService.update(serviceRequest);

        ChannelDto result = channelService.findById(channelId);

        return ResponseEntity.ok(new ChannelResponse(result));
    }

    @RequestMapping(
            value = "/{channelId}",
            method = RequestMethod.DELETE
    )
    public ResponseEntity<Void> delete(
            @PathVariable("channelId") UUID channelId
    ) {
        channelService.delete(channelId);

        return ResponseEntity.noContent().build();
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ChannelListResponse>> findAllByUserId(
            @RequestParam("userId") UUID userId
    ) {
        List<ChannelListResponse> channels = channelService.findAllByUserId(userId).stream()
                .map(ChannelListResponse::new)
                .toList();

        return ResponseEntity.ok(channels);
    }

    public record PrivateChannelBody(List<UUID> participantIds) {
    }

    public record PublicChannelUpdateBody(String newName, String newDescription) {
    }

    public record ChannelResponse(
            UUID id,
            Instant createdAt,
            Instant updatedAt,
            ChannelType type,
            String name,
            String description
    ) {
        public ChannelResponse(ChannelDto channel) {
            this(channel.id(), channel.createdAt(), channel.updatedAt(),
                    channel.type(), channel.name(), channel.description());
        }
    }

    public record ChannelListResponse(
            UUID id,
            ChannelType type,
            String name,
            String description,
            List<UUID> participantIds,
            Instant lastMessageAt
    ) {
        public ChannelListResponse(ChannelDto channel) {
            this(channel.id(), channel.type(), channel.name(), channel.description(),
                    channel.participantUserIds(), channel.lastMessageAt());
        }
    }
}

