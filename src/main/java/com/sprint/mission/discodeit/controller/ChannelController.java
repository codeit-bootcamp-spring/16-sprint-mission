package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.dto.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.UpdateChannelRequest;

import com.sprint.mission.discodeit.service.ChannelService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
public class ChannelController {

    private final ChannelService channelService;

    @RequestMapping(
            value = "/public",
            method = RequestMethod.POST
    )
    public ResponseEntity<ChannelResponse> createPublic(
            @RequestBody CreatePublicChannelRequest request
    ) {
        ChannelResponse response =
                channelService.createPublic(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @RequestMapping(
            value = "/private",
            method = RequestMethod.POST
    )
    public ResponseEntity<ChannelResponse> createPrivate(
            @RequestBody CreatePrivateChannelRequest request
    ) {
        ChannelResponse response =
                channelService.createPrivate(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<ChannelResponse> findAllByUserId(
            @RequestParam("userId") UUID userId
    ) {
        return channelService.findAllByUserId(userId);
    }

    @RequestMapping(
            value = "/{channelId}",
            method = RequestMethod.GET
    )
    public ChannelResponse findById(
            @PathVariable("channelId") UUID channelId
    ) {
        return channelService.findById(channelId);
    }

    @RequestMapping(
            value = "/{channelId}",
            method = RequestMethod.PATCH
    )
    public ChannelResponse update(
            @PathVariable("channelId") UUID channelId,
            @RequestBody UpdateChannelRequest request
    ) {
        if (request.id() != null
                && !channelId.equals(request.id())) {
            throw new IllegalArgumentException(
                    "URL의 채널 ID와 요청 본문의 ID가 다릅니다."
            );
        }

        UpdateChannelRequest updateRequest =
                new UpdateChannelRequest(
                        channelId,
                        request.name(),
                        request.description()
                );

        return channelService.update(updateRequest);
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

    @RequestMapping(
            value = "/{channelId}/members/{userId}",
            method = RequestMethod.POST
    )
    public ResponseEntity<Void> joinChannel(
            @PathVariable("channelId") UUID channelId,
            @PathVariable("userId") UUID userId
    ) {
        channelService.joinChannel(channelId, userId);

        return ResponseEntity.noContent().build();
    }

    @RequestMapping(
            value = "/{channelId}/members/{userId}",
            method = RequestMethod.DELETE
    )
    public ResponseEntity<Void> leaveChannel(
            @PathVariable("channelId") UUID channelId,
            @PathVariable("userId") UUID userId
    ) {
        channelService.leaveChannel(channelId, userId);

        return ResponseEntity.noContent().build();
    }
}