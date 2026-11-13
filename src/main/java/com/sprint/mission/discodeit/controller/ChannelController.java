package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ChannelDto> createChannel(
            @RequestBody PublicChannelCreateRequest request
    ) {
        ChannelDto result = channelService.createPublic(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @RequestMapping(
            value = "/private",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ChannelDto> createPrivate(
            @RequestBody PrivateChannelCreateRequest request
    ) {
        ChannelDto result = channelService.createPrivate(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @RequestMapping(
            value = "/{channelId}",
            method = RequestMethod.PATCH,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ChannelDto> update(
            @PathVariable("channelId") UUID channelId,
            @RequestBody ChannelUpdateRequest request
    ) {
        ChannelUpdateRequest serviceRequest = new ChannelUpdateRequest(
                channelId,
                request.name(),
                request.description()
        );
        channelService.update(serviceRequest);

        ChannelDto result = channelService.findById(channelId);

        return ResponseEntity.ok(result);
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
    public ResponseEntity<List<ChannelDto>> findAllByUserId(
            @RequestParam("userId") UUID userId
    ) {
        List<ChannelDto> channels = channelService.findAllByUserId(userId);

        return ResponseEntity.ok(channels);
    }
}

