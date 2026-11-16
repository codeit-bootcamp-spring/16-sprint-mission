package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.annotation.swagger.channel.ChannelDeleteDocs;
import com.sprint.mission.discodeit.annotation.swagger.channel.ChannelFindByUserIdDocs;
import com.sprint.mission.discodeit.annotation.swagger.channel.ChannelPrivateCreateDocs;
import com.sprint.mission.discodeit.annotation.swagger.channel.ChannelPublicCreateDocs;
import com.sprint.mission.discodeit.annotation.swagger.channel.ChannelUpdateDocs;
import com.sprint.mission.discodeit.dto.request.channels.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.request.channels.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channels.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.service.ChannelService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; // DTO의 유효성 검사를 실행하도록 지정
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
@Tag(name = "Channel", description = "Channel API")
public class ChannelController {

    private final ChannelService channelService;

    @ChannelPublicCreateDocs
    @RequestMapping(value = "/public", method = RequestMethod.POST)
    public ResponseEntity<ChannelDto> postPublicChannel(
            @Valid @RequestBody PublicChannelCreateRequest request) {

        ChannelDto channel = channelService.createPublicChannel(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(channel);
    }

    @ChannelPrivateCreateDocs
    @RequestMapping(value = "/private", method = RequestMethod.POST)
    public ResponseEntity<ChannelDto> postPrivateChannel(
            @Valid @RequestBody PrivateChannelCreateRequest request) {

        ChannelDto channel = channelService.createPrivateChannel(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(channel);
    }

    @ChannelFindByUserIdDocs
    @RequestMapping(method = RequestMethod.GET)
    public List<ChannelDto> getChannelByUserId(@RequestParam("userId") UUID userId) {
        return channelService.findAllByUserId(userId);
    }

    @ChannelUpdateDocs
    @RequestMapping(value = "/{channelId}", method = RequestMethod.PATCH)
    public ChannelDto patchChannel(
            @Parameter(description = "수정할 Channel ID", required = true)
            @PathVariable UUID channelId,
            @Valid @RequestBody PublicChannelUpdateRequest request) {

        return channelService.updateChannel(channelId, request);
    }

    @ChannelDeleteDocs
    @RequestMapping(value = "/{channelId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteChannel(
            @Parameter(description = "삭제할 Channel ID", required = true)
            @PathVariable UUID channelId) {

        channelService.deleteChannel(channelId);
        return ResponseEntity.noContent().build();
    }
}