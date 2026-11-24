package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ApiSpecDto;
import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.dto.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.UpdateChannelRequest;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import com.sprint.mission.discodeit.exception.ResourceNotFoundException;

import com.sprint.mission.discodeit.repository.ChannelRepository;

import com.sprint.mission.discodeit.service.ChannelService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

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
@Tag(name = "Channel", description = "Channel API")
public class ChannelController {

    private final ChannelService channelService;
    private final ChannelRepository channelRepository;

    @Operation(summary = "User가 참여 중인 Channel 목록 조회")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ApiSpecDto.ChannelSummary>> findAll(
            @RequestParam("userId") UUID userId
    ) {
        List<ApiSpecDto.ChannelSummary> channels =
                channelService.findAllByUserId(userId)
                        .stream()
                        .map(this::toChannelSummary)
                        .toList();

        return ResponseEntity.ok(channels);
    }

    @Operation(summary = "Public Channel 생성")
    @RequestMapping(
            value = "/public",
            method = RequestMethod.POST
    )
    public ResponseEntity<ApiSpecDto.ChannelView> createPublic(
            @RequestBody ApiSpecDto.PublicChannelCreate request
    ) {
        ChannelResponse channel =
                channelService.createPublic(
                        new CreatePublicChannelRequest(
                                request.name(),
                                request.description()
                        )
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toChannelView(channel.id()));
    }

    @Operation(summary = "Private Channel 생성")
    @RequestMapping(
            value = "/private",
            method = RequestMethod.POST
    )
    public ResponseEntity<ApiSpecDto.ChannelView> createPrivate(
            @RequestBody ApiSpecDto.PrivateChannelCreate request
    ) {
        if (request == null
                || request.participantIds() == null
                || request.participantIds().isEmpty()
                || request.participantIds()
                .stream()
                .anyMatch(id -> id == null)) {
            throw new IllegalArgumentException(
                    "유효한 참여자 ID 목록이 필요합니다."
            );
        }

        ChannelResponse channel =
                channelService.createPrivate(
                        new CreatePrivateChannelRequest(
                                request.participantIds()
                        )
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toChannelView(channel.id()));
    }

    @Operation(summary = "Channel 정보 수정")
    @RequestMapping(
            value = "/{channelId}",
            method = RequestMethod.PATCH
    )
    public ResponseEntity<ApiSpecDto.ChannelView> update(
            @PathVariable("channelId") UUID channelId,
            @RequestBody ApiSpecDto.PublicChannelUpdate request
    ) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "채널 수정 정보가 없습니다."
            );
        }

        ChannelResponse current =
                channelService.findById(channelId);

        if (current.type() == ChannelType.PRIVATE) {
            throw new IllegalArgumentException(
                    "PRIVATE 채널은 수정할 수 없습니다."
            );
        }

        String name = request.newName() == null
                ? current.name()
                : request.newName();

        String description =
                request.newDescription() == null
                        ? current.description()
                        : request.newDescription();

        channelService.update(
                new UpdateChannelRequest(
                        channelId,
                        name,
                        description
                )
        );

        return ResponseEntity.ok(
                toChannelView(channelId)
        );
    }

    @Operation(summary = "Channel 삭제")
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

    private ApiSpecDto.ChannelSummary toChannelSummary(
            ChannelResponse channel
    ) {
        return new ApiSpecDto.ChannelSummary(
                channel.id(),
                channel.type(),
                channel.name(),
                channel.description(),
                channel.participantIds(),
                channel.lastMessageAt()
        );
    }

    private ApiSpecDto.ChannelView toChannelView(
            UUID channelId
    ) {
        Channel channel = channelRepository.findById(channelId);

        if (channel == null) {
            throw new ResourceNotFoundException(
                    "채널을 찾을 수 없습니다. ID: " + channelId
            );
        }

        return new ApiSpecDto.ChannelView(
                channel.getId(),
                channel.getCreatedAt(),
                channel.getUpdatedAt(),
                channel.getType(),
                channel.getName(),
                channel.getDescription()
        );
    }
}